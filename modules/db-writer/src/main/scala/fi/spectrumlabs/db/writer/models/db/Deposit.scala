package fi.spectrumlabs.db.writer.models.db

import fi.spectrumlabs.db.writer.classes.FromLedger
import fi.spectrumlabs.db.writer.models.orders.{Amount, PoolId}
import fi.spectrumlabs.db.writer.models.streaming.{ExecutedDeposit, ExecutedOrderEvent}
import io.circe.parser.parse
import fi.spectrumlabs.db.writer.models.orders.AssetClass.syntax._

final case class Deposit(
  poolId: PoolId,
  depositX: String,
  depositY: String,
  depositLq: String,
  xAmount: Amount,
  yAmount: Amount,
  lqAmount: Amount,
  exFee: Long,
  rewardPkh: String,
  stakePkh: Option[String],
  collateralAda: Long,
  depositOrderInputId: String,
  depositUserOutputId: String
)

object Deposit {

  implicit val fromLedger: FromLedger[ExecutedOrderEvent, Option[Deposit]] =
    (in: ExecutedOrderEvent) =>
      parse(in.stringJson).toOption
        .flatMap(_.as[ExecutedDeposit].toOption)
        .map { op =>
          Deposit(
            op.depositCfg.depositPoolId,
            op.depositCfg.depositPair._1.value._1.asName,
            op.depositCfg.depositPair._2.value._1.asName,
            op.lqReward.value._1.asName,
            op.depositCfg.depositPair._1.value._2,
            op.depositCfg.depositPair._2.value._2,
            op.lqReward.value._2,
            op.depositCfg.depositExFee,
            op.depositCfg.depositRewardPkh,
            op.depositCfg.depositRewardSPkh,
            op.depositCfg.adaCollateral,
            op.depositOrderInputId,
            op.depositUserOutputId
          )
      }
}
