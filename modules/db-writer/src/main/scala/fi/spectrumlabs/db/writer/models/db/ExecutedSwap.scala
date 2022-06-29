package fi.spectrumlabs.db.writer.models.db

import fi.spectrumlabs.db.writer.classes.ToSchema
import fi.spectrumlabs.db.writer.models.orders._
import fi.spectrumlabs.db.writer.models.streaming
import fi.spectrumlabs.db.writer.models.streaming.{ExecutedOrderEvent => Executed}
import cats.syntax.option._
import fi.spectrumlabs.core.models.domain.{Amount, AssetClass, Coin}
import fi.spectrumlabs.core.models.domain.AssetClass.syntax._

final case class ExecutedSwap(
  base: Coin,
  quote: Coin,
  poolId: Coin,
  exFeePerTokenNum: Long,
  exFeePerTokenDen: Long,
  rewardPkh: String,
  stakePkh: Option[StakePKH],
  baseAmount: Amount,
  actualQuote: Amount,
  minQuoteAmount: Amount,
  orderInputId: TxOutRef,
  userOutputId: TxOutRef,
  poolInputId: TxOutRef,
  poolOutputId: TxOutRef,
  timestamp: Long
)

object ExecutedSwap {

  implicit val toSchema: ToSchema[streaming.ExecutedOrderEvent, Option[ExecutedSwap]] = {
    case order: Executed.ExecutedSwap =>
      ExecutedSwap(
        order.swap.config.base.toCoin,
        order.swap.config.quote.toCoin,
        order.swap.config.poolId.toCoin,
        order.swap.config.exFee.exFeePerTokenNum,
        order.swap.config.exFee.exFeePerTokenDen,
        order.swap.config.rewardPkh.toString,
        order.swap.config.rewardSPkh,
        order.swap.config.baseIn,
        order.actualQuote,
        order.swap.config.minQuoteOut,
        order.swap.orderInputId,
        order.swap.userOutputId,
        order.swap.poolInputId,
        order.swap.poolOutputId,
        order.swap.timestamp
      ).some
    case _ => none
  }
}
