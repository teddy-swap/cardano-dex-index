package fi.spectrumlabs.db.writer.models.db

import fi.spectrumlabs.db.writer.classes.FromLedger
import fi.spectrumlabs.db.writer.models.orders._
import fi.spectrumlabs.db.writer.models.streaming
import io.circe.parser.parse
import cats.syntax.either._
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
  poolOutputId: TxOutRef
)

object ExecutedRedeem {

  implicit val fromLedger: FromLedger[streaming.ExecutedOrderEvent, Option[ExecutedRedeem]] =
    (in: streaming.ExecutedOrderEvent) =>
      parse(in.stringJson)
        .flatMap(_.as[streaming.ExecutedRedeem])
        .toOption
        .map { redeem =>
          ExecutedRedeem(
            redeem.redeem.config.poolId.toCoin,
            redeem.rewardX.asset.toCoin,
            redeem.rewardY.asset.toCoin,
            redeem.redeem.config.lq.toCoin,
            redeem.rewardX.amount,
            redeem.rewardY.amount,
            redeem.redeem.config.lqIn,
            redeem.redeem.config.exFee,
            redeem.redeem.config.rewardPkh,
            redeem.redeem.config.rewardSPkh,
            redeem.redeem.orderInputId,
            redeem.redeem.userOutputId,
            redeem.redeem.poolInputId,
            redeem.redeem.poolOutputId
          )
      }
}
