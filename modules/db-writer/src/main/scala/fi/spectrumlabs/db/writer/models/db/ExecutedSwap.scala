package fi.spectrumlabs.db.writer.models.db

import fi.spectrumlabs.db.writer.classes.FromLedger
import fi.spectrumlabs.db.writer.models.orders._
import fi.spectrumlabs.db.writer.models.streaming
import io.circe.parser.parse

final case class ExecutedSwap(
  base: Coin,
  quote: Coin,
  poolId: PoolId,
  exFeePerTokenNum: ExFee,
  exFeePerTokenDen: ExFee,
  rewardPkh: PublicKeyHash,
  stakePkh: Option[PublicKeyHash],
  baseAmount: Amount,
  actualQuote: Amount,
  minQuoteAmount: Amount,
  orderInputId: BoxId,
  userOutputId: BoxId,
  poolInputId: BoxId,
  poolOutputId: BoxId
)

object ExecutedSwap {

  implicit val fromLedger: FromLedger[streaming.ExecutedOrderEvent, Option[ExecutedSwap]] =
    (in: streaming.ExecutedOrderEvent) =>
      parse(in.stringJson).toOption
        .flatMap(_.as[streaming.ExecutedSwap].toOption)
        .map { swap =>
          ExecutedSwap(
            swap.config.swapBase,
            swap.config.swapQuote,
            swap.config.swapPoolId,
            swap.config.swapExFee.exFeePerTokenNum,
            swap.config.swapExFee.exFeePerTokenDen,
            swap.config.swapRewardPkh,
            swap.config.swapRewardSPkh,
            swap.config.swapBaseIn,
            swap.actualQuote,
            swap.config.swapMinQuoteOut,
            swap.orderInputId,
            swap.userOutputId,
            swap.poolInputId,
            swap.poolOutputId
          )
      }
}
