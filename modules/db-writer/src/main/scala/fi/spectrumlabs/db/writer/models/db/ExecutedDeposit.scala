package fi.spectrumlabs.db.writer.models.db

import fi.spectrumlabs.db.writer.classes.FromLedger
import fi.spectrumlabs.db.writer.models.orders._
import fi.spectrumlabs.db.writer.models.streaming
import io.circe.parser.parse
import fi.spectrumlabs.db.writer.models.orders.AssetClass.syntax._

final case class ExecutedDeposit(
  poolId: Coin,
  coinX: Coin,
  coinY: Coin,
  coinLq: Coin,
  amountX: Amount,
  amountY: Amount,
  amountLq: Amount,
  exFee: ExFee,
  rewardPkh: String,
  stakePkh: Option[StakePKH],
  collateralAda: Long,
  orderInputId: TxOutRef,
  userOutputId: TxOutRef,
  poolInputId: TxOutRef,
  poolOutputId: TxOutRef,
  timestamp: Long
)

object ExecutedDeposit {

  implicit val fromLedger: FromLedger[streaming.ExecutedOrderEvent, Option[ExecutedDeposit]] =
    (in: streaming.ExecutedOrderEvent) =>
      parse(in.stringJson)
        .flatMap(_.as[streaming.ExecutedDeposit])
        .toOption
        .map { order =>
          ExecutedDeposit(
            order.deposit.config.poolId.toCoin,
            order.deposit.config.x.asset.toCoin,
            order.deposit.config.y.asset.toCoin,
            order.rewardLq.asset.toCoin,
            order.deposit.config.x.amount,
            order.deposit.config.y.amount,
            order.rewardLq.amount,
            order.deposit.config.exFee,
            order.deposit.config.rewardPkh.toString,
            order.deposit.config.rewardSPkh,
            order.deposit.config.adaCollateral,
            order.deposit.orderInputId,
            order.deposit.userOutputId,
            order.deposit.poolInputId,
            order.deposit.poolOutputId,
            order.deposit.timestamp
          )
      }
}
