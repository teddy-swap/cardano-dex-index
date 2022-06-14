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
  poolOutputId: TxOutRef
)

object ExecutedDeposit {

  implicit val fromLedger: FromLedger[streaming.ExecutedOrderEvent, Option[ExecutedDeposit]] =
    (in: streaming.ExecutedOrderEvent) =>
      parse(in.stringJson)
        .flatMap(_.as[streaming.ExecutedDeposit])
        .toOption
        .map { deposit =>
          ExecutedDeposit(
            deposit.deposit.config.poolId.toCoin,
            deposit.deposit.config.x.asset.toCoin,
            deposit.deposit.config.y.asset.toCoin,
            deposit.rewardLq.asset.toCoin,
            deposit.deposit.config.x.amount,
            deposit.deposit.config.y.amount,
            deposit.rewardLq.amount,
            deposit.deposit.config.exFee,
            deposit.deposit.config.rewardPkh.toString,
            deposit.deposit.config.rewardSPkh,
            deposit.deposit.config.adaCollateral,
            deposit.deposit.orderInputId,
            deposit.deposit.userOutputId,
            deposit.deposit.poolInputId,
            deposit.deposit.poolOutputId
          )
      }
}
