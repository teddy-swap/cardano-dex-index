package fi.spectrumlabs.db.writer.models.db

import fi.spectrumlabs.db.writer.classes.FromLedger
import fi.spectrumlabs.db.writer.models.orders._
import fi.spectrumlabs.db.writer.models.streaming
import io.circe.parser.parse
import fi.spectrumlabs.db.writer.models.orders.AssetClass.syntax._

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

  implicit val fromLedger: FromLedger[streaming.ExecutedOrderEvent, Option[ExecutedSwap]] =
    (in: streaming.ExecutedOrderEvent) => {
      parse(in.stringJson)
        .flatMap(_.as[streaming.ExecutedSwap])
        .toOption
        .map { order =>
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
          )
        }
    }
}
