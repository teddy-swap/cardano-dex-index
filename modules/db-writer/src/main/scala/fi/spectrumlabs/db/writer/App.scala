package fi.spectrumlabs.db.writer

import cats.data.NonEmptyList
import cats.effect.{Blocker, Resource}
import fi.spectrumlabs.core.EnvApp
import fi.spectrumlabs.core.models.Tx
import fi.spectrumlabs.core.streaming.config.{ConsumerConfig, KafkaConfig}
import fi.spectrumlabs.core.streaming.{Consumer, MakeKafkaConsumer}
import fi.spectrumlabs.db.writer.classes.Handle
import fi.spectrumlabs.db.writer.config._
import fi.spectrumlabs.db.writer.models._
import fi.spectrumlabs.db.writer.persistence.PersistBundle
import fi.spectrumlabs.db.writer.programs.{Handler, HandlersBundle, WriterProgram}
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
import fi.spectrumlabs.core.streaming.serde._
import fi.spectrumlabs.db.writer.models.db.{Deposit, Redeem, Swap}
import fi.spectrumlabs.db.writer.models.streaming.ExecutedOrderEvent

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
      implicit0(txConsumer: Consumer[String, Option[Tx], StreamF, RunF]) = makeConsumer[String, Option[Tx]](
                                                                             configs.txConsumer,
                                                                             configs.kafka
                                                                           )
      implicit0(executedOpsConsumer: Consumer[String, Option[ExecutedOrderEvent], StreamF, RunF]) =
        makeConsumer[
          String,
          Option[ExecutedOrderEvent]
        ](
          configs.executedOpsConsumer,
          configs.kafka
        )
      implicit0(persistBundle: PersistBundle[RunF]) = PersistBundle.create[xa.DB, RunF]
      txHandler          <- makeTxHandler(configs.writer)
      executedOpsHandler <- makeExecutedOrdersHandler(configs.writer)
      bundle  = HandlersBundle.make[StreamF](txHandler, executedOpsHandler)
      program = WriterProgram.create[StreamF, RunF](bundle, configs.writer)
      r <- Resource.eval(program.run).mapK(ul.liftF)
    } yield r

  private def makeTxHandler(config: WriterConfig)(implicit
    bundle: PersistBundle[RunF],
    consumer: Consumer[_, Option[Tx], StreamF, RunF]
  ) = Resource.eval {
    import bundle._
    for {
      txn  <- Handle.createOne[Tx, Transaction, InitF, RunF](transaction)
      in   <- Handle.createNel[Tx, Input, InitF, RunF](input)
      out  <- Handle.createNel[Tx, Output, InitF, RunF](output)
      reed <- Handle.createList[Tx, Redeemer, InitF, RunF](redeemer)
      implicit0(nelHandlers: NonEmptyList[Handle[Tx, RunF]]) = NonEmptyList.of(txn, in, out, reed)
      handler <- Handler.create[Tx, StreamF, RunF, Chunk, InitF](config)
    } yield handler
  }

  private def makeExecutedOrdersHandler(config: WriterConfig)(implicit
    bundle: PersistBundle[RunF],
    consumer: Consumer[_, Option[ExecutedOrderEvent], StreamF, RunF]
  ) = Resource.eval {
    import bundle._
    for {
      deposit <- Handle.createOption[ExecutedOrderEvent, Deposit, InitF, RunF](executedDeposit)
      swap    <- Handle.createOption[ExecutedOrderEvent, Swap, InitF, RunF](executedSwap)
      redeem  <- Handle.createOption[ExecutedOrderEvent, Redeem, InitF, RunF](executedRedeem)
      implicit0(nelHandlers: NonEmptyList[Handle[ExecutedOrderEvent, RunF]]) = NonEmptyList.of(deposit, swap, redeem)
      handler <- Handler.create[ExecutedOrderEvent, StreamF, RunF, Chunk, InitF](config)
    } yield handler
  }

  private def makeConsumer[K: RecordDeserializer[RunF, *], V: RecordDeserializer[RunF, *]](
    conf: ConsumerConfig,
    kafka: KafkaConfig
  ) = {
    implicit val maker = MakeKafkaConsumer.make[InitF, RunF, K, V](kafka)
    Consumer.make[StreamF, RunF, K, V](conf)
  }
}
