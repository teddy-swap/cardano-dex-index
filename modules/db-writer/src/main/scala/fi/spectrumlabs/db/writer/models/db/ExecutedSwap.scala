package fi.spectrumlabs.db.writer.models.db

import fi.spectrumlabs.db.writer.classes.FromLedger
import fi.spectrumlabs.db.writer.models.orders._
import fi.spectrumlabs.db.writer.models.streaming
import io.circe.parser.parse
import cats.syntax.either._
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
  poolOutputId: TxOutRef
)

object ExecutedSwap {

  implicit val fromLedger: FromLedger[streaming.ExecutedOrderEvent, Option[ExecutedSwap]] =
    (in: streaming.ExecutedOrderEvent) => {
      parse(in.stringJson)
        .flatMap(_.as[streaming.ExecutedSwap])
        .toOption
        .map { swap =>
          ExecutedSwap(
            swap.swap.config.base.toCoin,
            swap.swap.config.quote.toCoin,
            swap.swap.config.poolId.toCoin,
            swap.swap.config.exFee.exFeePerTokenNum,
            swap.swap.config.exFee.exFeePerTokenDen,
            swap.swap.config.rewardPkh.toString,
            swap.swap.config.rewardSPkh,
            swap.swap.config.baseIn,
            swap.actualQuote,
            swap.swap.config.minQuoteOut,
            swap.swap.orderInputId,
            swap.swap.userOutputId,
            swap.swap.poolInputId,
            swap.swap.poolOutputId
          )
        }
    }
}
