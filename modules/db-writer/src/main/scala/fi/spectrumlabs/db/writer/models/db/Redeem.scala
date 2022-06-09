package fi.spectrumlabs.db.writer.models.db

import fi.spectrumlabs.db.writer.classes.FromLedger
import fi.spectrumlabs.db.writer.models.orders.{Amount, PoolId}
import fi.spectrumlabs.db.writer.models.streaming.{ExecutedOrderEvent, ExecutedRedeem}
import io.circe.parser.parse

final case class Redeem(
  poolId: PoolId,
  redeemX: String,
  redeemY: String,
  redeemLq: String,
  xAmount: Amount,
  yAmount: Amount,
  lqAmount: Amount,
  exFee: Long,
  rewardPkh: String,
  stakePkh: Option[String],
  redeemOrderInputId: String,
  redeemUserOutputId: String
)

object Redeem {

  implicit val fromLedger: FromLedger[ExecutedOrderEvent, Option[Redeem]] =
    (in: ExecutedOrderEvent) =>
      parse(in.stringJson).toOption
        .flatMap(_.as[ExecutedRedeem].toOption)
        .map { op =>
          Redeem(
            op.redeemCfg.redeemPoolId,
            op.xReward.getAsset.value,
            op.yReward.getAsset.value,
            op.redeemCfg.redeemLq.value,
            op.xReward.getAmount,
            op.yReward.getAmount,
            op.redeemCfg.redeemLqIn,
            op.redeemCfg.redeemExFee,
            op.redeemCfg.redeemRewardPkh,
            op.redeemCfg.redeemRewardSPkh,
            op.redeemOrderInputId,
            op.redeemUserOutputId
          )
        }
}
