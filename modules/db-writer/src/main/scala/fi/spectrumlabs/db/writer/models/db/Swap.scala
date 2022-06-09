package fi.spectrumlabs.db.writer.models.db

import fi.spectrumlabs.db.writer.classes.FromLedger
import fi.spectrumlabs.db.writer.models.orders.{Amount, Coin, PoolId}
import fi.spectrumlabs.db.writer.models.streaming.{ExecutedOrderEvent, ExecutedSwap}
import io.circe.parser.parse

final case class Swap(
  base: Coin,
  quote: Coin,
  poolId: PoolId,
  exFeePerTokenNum: Long,
  exFeePerTokenDen: Long,
  rewardPkh: String,
  stakePkh: Option[String],
  baseAmount: Amount,
  actualQuote: Amount,
  minQuoteAmount: Amount,
  swapOrderInputId: String,
  swapUserOutputId: String
)

object Swap {

  implicit val fromLedger: FromLedger[ExecutedOrderEvent, Option[Swap]] =
    (in: ExecutedOrderEvent) =>
      parse(in.stringJson).toOption
        .flatMap(_.as[ExecutedSwap].toOption)
        .map { op =>
          Swap(
            op.swapCfg.swapBase,
            op.swapCfg.swapQuote,
            op.swapCfg.swapPoolId,
            op.swapCfg.swapExFee.exFeePerTokenNum,
            op.swapCfg.swapExFee.exFeePerTokenDen,
            op.swapCfg.swapRewardPkh,
            op.swapCfg.swapRewardSPkh,
            op.swapCfg.swapBaseIn,
            op.actualQuote,
            op.swapCfg.swapMinQuoteOut,
            op.swapOrderInputId,
            op.swapUserOutputId
          )
        }
}
