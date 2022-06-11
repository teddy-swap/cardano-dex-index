package fi.spectrumlabs.db.writer.models.db

import fi.spectrumlabs.db.writer.classes.FromLedger
import fi.spectrumlabs.db.writer.models.orders._
import fi.spectrumlabs.db.writer.models.streaming
import io.circe.parser.parse

final case class ExecutedRedeem(
  poolId: PoolId,
  coinX: Coin,
  coinY: Coin,
  coinLq: Coin,
  amountX: Amount,
  amountY: Amount,
  amountLq: Amount,
  exFee: ExFee,
  rewardPkh: PublicKeyHash,
  stakePkh: Option[PublicKeyHash],
  orderInputId: BoxId,
  userOutputId: BoxId,
  poolInputId: BoxId,
  poolOutputId: BoxId
)

object ExecutedRedeem {

  implicit val fromLedger: FromLedger[streaming.ExecutedOrderEvent, Option[ExecutedRedeem]] =
    (in: streaming.ExecutedOrderEvent) =>
      parse(in.stringJson).toOption
        .flatMap(_.as[streaming.ExecutedRedeem].toOption)
        .map { redeem =>
          ExecutedRedeem(
            redeem.config.redeemPoolId,
            redeem.rewardX.getAsset,
            redeem.rewardY.getAsset,
            redeem.config.redeemLq,
            redeem.rewardX.getAmount,
            redeem.rewardY.getAmount,
            redeem.config.redeemLqIn,
            redeem.config.redeemExFee,
            redeem.config.redeemRewardPkh,
            redeem.config.redeemRewardSPkh,
            redeem.orderInputId,
            redeem.userOutputId,
            redeem.poolInputId,
            redeem.poolOutputId
          )
      }
}
