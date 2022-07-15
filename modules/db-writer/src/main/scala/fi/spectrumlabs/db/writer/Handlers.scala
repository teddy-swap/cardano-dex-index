package fi.spectrumlabs.db.writer

import cats.data.NonEmptyList
import cats.effect.Resource
import fi.spectrumlabs.core.models.Tx
import fi.spectrumlabs.core.streaming.Consumer
import fi.spectrumlabs.db.writer.App.{InitF, RunF, StreamF}
import fi.spectrumlabs.db.writer.classes.Handle
import fi.spectrumlabs.db.writer.config.WriterConfig
import fi.spectrumlabs.db.writer.models.db.{ExecutedDeposit, ExecutedRedeem, ExecutedSwap, Pool}
import fi.spectrumlabs.db.writer.models.streaming.{ExecutedOrderEvent, PoolEvent}
import fi.spectrumlabs.db.writer.models.{Input, Output, Redeemer, Transaction}
import fi.spectrumlabs.db.writer.persistence.PersistBundle
import fi.spectrumlabs.db.writer.programs.Handler
import fs2.Chunk
import tofu.WithContext
import tofu.fs2Instances._
import tofu.logging.Logs
import zio.interop.catz._

object Handlers {

  val TxHandlerName             = "Tx"
  val ExecutedOrdersHandlerName = "ExecutedOrder"
  val PoolsHandler              = "PoolsHandler"
  val TxHandleName              = "Transaction"
  val InHandleName              = "Input"
  val OutHandleName             = "Output"
  val ReedHandleName            = "Redeemer"
  val DepositHandleName         = "Deposit"
  val SwapHandleName            = "Swap"
  val RedeemHandleName          = "Redeem"
  val PoolHandleName            = "Pool"

  def makeTxHandler(config: WriterConfig)(implicit
    bundle: PersistBundle[RunF],
    consumer: Consumer[_, Option[Tx], StreamF, RunF],
    logs: Logs[InitF, RunF]
  ): Resource[InitF, Handler[StreamF]] = Resource.eval {
    import bundle._
    for {
      txn  <- Handle.createOne[Tx, Transaction, InitF, RunF](transaction, TxHandleName)
      in   <- Handle.createNel[Tx, Input, InitF, RunF](input, InHandleName)
      out  <- Handle.createNel[Tx, Output, InitF, RunF](output, OutHandleName)
      reed <- Handle.createList[Tx, Redeemer, InitF, RunF](redeemer, ReedHandleName)
      implicit0(nelHandlers: NonEmptyList[Handle[Tx, RunF]]) = NonEmptyList.of(txn, in, out, reed)
      handler <- Handler.create[Tx, StreamF, RunF, Chunk, InitF](config, TxHandlerName)
    } yield handler
  }

  def makeExecutedOrdersHandler(config: WriterConfig)(implicit
    bundle: PersistBundle[RunF],
    consumer: Consumer[_, Option[ExecutedOrderEvent], StreamF, RunF],
    logs: Logs[InitF, RunF]
  ): Resource[InitF, Handler[StreamF]] = Resource.eval {
    import bundle._
    for {
      deposit <-
        Handle.createOption[ExecutedOrderEvent, ExecutedDeposit, InitF, RunF](executedDeposit, DepositHandleName)
      swap   <- Handle.createOption[ExecutedOrderEvent, ExecutedSwap, InitF, RunF](executedSwap, SwapHandleName)
      redeem <- Handle.createOption[ExecutedOrderEvent, ExecutedRedeem, InitF, RunF](executedRedeem, RedeemHandleName)
      implicit0(nelHandlers: NonEmptyList[Handle[ExecutedOrderEvent, RunF]]) = NonEmptyList.of(deposit, swap, redeem)
      handler <- Handler.create[ExecutedOrderEvent, StreamF, RunF, Chunk, InitF](config, ExecutedOrdersHandlerName)
    } yield handler
  }

  def makePoolsHandler(config: WriterConfig)(implicit
    bundle: PersistBundle[RunF],
    consumer: Consumer[_, Option[PoolEvent], StreamF, RunF],
    logs: Logs[InitF, RunF]
  ): Resource[InitF, Handler[StreamF]] = Resource.eval {
    import bundle._
    for {
      pool <- Handle.createOne[PoolEvent, Pool, InitF, RunF](pool, PoolHandleName)
      implicit0(nelHandlers: NonEmptyList[Handle[PoolEvent, RunF]]) = NonEmptyList.of(pool)
      handler <- Handler.create[PoolEvent, StreamF, RunF, Chunk, InitF](config, PoolsHandler)
    } yield handler
  }

}
