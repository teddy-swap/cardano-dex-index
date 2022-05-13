package fi.spectrumlabs.db.writer

import cats.data.NonEmptyList
import cats.effect.{Blocker, Resource}
import fi.spectrumlabs.core.models.Transaction
import fi.spectrumlabs.db.writer.config.{AppContext, ConfigBundle, ConsumerConfig, KafkaConfig}
import fi.spectrumlabs.db.writer.persistence.{Persist, PersistBundle}
import fi.spectrumlabs.db.writer.programs.{Handler, HandlersBundle, WriterProgram}
import fi.spectrumlabs.db.writer.schema.{Schema, SchemaBundle}
import fi.spectrumlabs.db.writer.streaming.{Consumer, MakeKafkaConsumer}
import fs2.Chunk
import fs2.kafka.RecordDeserializer
import tofu.doobie.log.EmbeddableLogHandler
import tofu.doobie.transactor.Txr
import tofu.lift.{IsoK, Unlift}
import tofu.logging.Logs
import tofu.logging.derivation.loggable.generate
import zio.interop.catz._
import zio.{ExitCode, URIO, ZIO}
import tofu.doobie.log.EmbeddableLogHandler
import tofu.fs2Instances._
import zio.interop.catz._
import doobie.implicits._
import fi.spectrumlabs.db.writer

object App extends EnvApp[AppContext] {

  def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    init(args.headOption).use(_ => ZIO.never).orDie

  def init(configPathOpt: Option[String]): Resource[InitF, Unit] =
    for {
      blocker <- Blocker[InitF]
      configs <- Resource.eval(ConfigBundle.load[InitF](configPathOpt, blocker))
      ctx                                = AppContext.init(configs)
      implicit0(ul: Unlift[RunF, InitF]) = Unlift.byIso(IsoK.byFunK(wr.runContextK(ctx))(wr.liftF))
      trans <- PostgresTransactor.make[InitF]("meta-db-pool", configs.pg)
      implicit0(xa: Txr.Continuational[RunF]) = Txr.continuational[RunF](trans.mapK(wr.liftF))
      implicit0(elh: EmbeddableLogHandler[xa.DB]) <- Resource.eval(
                                                      doobieLogging.makeEmbeddableHandler[InitF, RunF, xa.DB](
                                                        "db-writer-db-logging"
                                                      )
                                                    )
      implicit0(logsDb: Logs[InitF, xa.DB]) = Logs.sync[InitF, xa.DB]

      implicit0(consumer: Consumer[String, Transaction, StreamF, RunF]) = makeConsumer[String, Transaction](
        configs.tnxConsumer,
        configs.kafka
      )

      schemaBundle = SchemaBundle.create

      persistBundle: PersistBundle[RunF] = PersistBundle.create[xa.DB, RunF](schemaBundle, xa.trans)

      handler = Handler.create[StreamF, RunF, Chunk](consumer, persistBundle)

      _ <- Resource.eval(handler.handle.compile.drain).mapK(ul.liftF)
    } yield ()

  private def makeConsumer[K: RecordDeserializer[RunF, *], V: RecordDeserializer[RunF, *]](
    conf: ConsumerConfig,
    kafka: KafkaConfig
  ) = {
    implicit val maker = MakeKafkaConsumer.make[InitF, RunF, K, V](kafka)
    Consumer.make[StreamF, RunF, K, V](conf)
  }
}
