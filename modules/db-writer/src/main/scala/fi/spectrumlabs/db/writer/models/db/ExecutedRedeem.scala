package fi.spectrumlabs.db.writer.models.db

import fi.spectrumlabs.db.writer.classes.FromLedger
import fi.spectrumlabs.db.writer.models.orders._
import fi.spectrumlabs.db.writer.models.streaming
import io.circe.parser.parse
import fi.spectrumlabs.db.writer.models.orders.AssetClass.syntax._

final case class ExecutedRedeem(
  poolId: Coin,
  coinX: Coin,
  coinY: Coin,
  coinLq: Coin,
  amountX: Amount,
  amountY: Amount,
  amountLq: Amount,
  exFee: ExFee,
  rewardPkh: PublicKeyHash,
  stakePkh: Option[StakePKH],
  orderInputId: TxOutRef,
  userOutputId: TxOutRef,
  poolInputId: TxOutRef,
  poolOutputId: TxOutRef,
  timestamp: Long
)

object ExecutedRedeem {

  implicit val fromLedger: FromLedger[streaming.ExecutedOrderEvent, Option[ExecutedRedeem]] =
    (in: streaming.ExecutedOrderEvent) =>
      parse(in.stringJson)
        .flatMap(_.as[streaming.ExecutedRedeem])
        .toOption
        .map { order =>
          ExecutedRedeem(
            order.redeem.config.poolId.toCoin,
            order.rewardX.asset.toCoin,
            order.rewardY.asset.toCoin,
            order.redeem.config.lq.toCoin,
            order.rewardX.amount,
            order.rewardY.amount,
            order.redeem.config.lqIn,
            order.redeem.config.exFee,
            order.redeem.config.rewardPkh,
            order.redeem.config.rewardSPkh,
            order.redeem.orderInputId,
            order.redeem.userOutputId,
            order.redeem.poolInputId,
            order.redeem.poolOutputId,
            order.redeem.timestamp
          )
      }
}
