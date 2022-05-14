package fi.spectrumlabs.db.writer

import cats.data.NonEmptyList
import cats.effect.{Blocker, Resource}
import fi.spectrumlabs.core.models.{Transaction => Tx}
import fi.spectrumlabs.db.writer.classes.Handle
import fi.spectrumlabs.db.writer.config.{AppContext, ConfigBundle, ConsumerConfig, KafkaConfig}
import fi.spectrumlabs.db.writer.models._
import fi.spectrumlabs.db.writer.persistence.PersistBundle
import fi.spectrumlabs.db.writer.programs.Handler
import fi.spectrumlabs.db.writer.schema.SchemaBundle
import fi.spectrumlabs.db.writer.streaming.{Consumer, MakeKafkaConsumer}
import fs2.Chunk
import fs2.kafka.RecordDeserializer
import tofu.doobie.log.EmbeddableLogHandler
import tofu.doobie.transactor.Txr
import tofu.fs2Instances._
import tofu.lift.{IsoK, Unlift}
import tofu.logging.Logs
import tofu.logging.derivation.loggable.generate
import zio.interop.catz._
import zio.{ExitCode, URIO, ZIO}

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
      implicit0(consumer: Consumer[String, Tx, StreamF, RunF]) = makeConsumer[String, Tx](
        configs.tnxConsumer,
        configs.kafka
      )
      implicit0(schemaBundle: SchemaBundle)         = SchemaBundle.create
      implicit0(persistBundle: PersistBundle[RunF]) = PersistBundle.create[xa.DB, RunF]
      handler                                       = makeHandler
      _ <- Resource.eval(handler.handle.compile.drain).mapK(ul.liftF)
    } yield ()

  private def makeHandler(
    implicit
    bundle: PersistBundle[RunF],
    consumer: Consumer[_, Tx, StreamF, RunF]
  ): Handler[StreamF] = {
    import bundle._
    implicit val nelHandlers: NonEmptyList[Handle[Tx, RunF]] = NonEmptyList.of(
      Handle.createOne[Tx, Transaction, RunF](transaction),
      Handle.createMany[Tx, Input, RunF](input),
      Handle.createMany[Tx, Output, RunF](output),
      Handle.createMany[Tx, Redeemer, RunF](redeemer)
    )
    Handler.create[StreamF, RunF, Chunk]
  }

  private def makeConsumer[K: RecordDeserializer[RunF, *], V: RecordDeserializer[RunF, *]](
    conf: ConsumerConfig,
    kafka: KafkaConfig
  ) = {
    implicit val maker = MakeKafkaConsumer.make[InitF, RunF, K, V](kafka)
    Consumer.make[StreamF, RunF, K, V](conf)
  }
}
