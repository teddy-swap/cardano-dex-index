package fi.spectrumlabs.db.writer.models.streaming

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fi.spectrumlabs.db.writer.models.orders.{Amount, AssetAmount, Deposit, Redeem, Swap}
import tofu.logging.derivation.loggable

sealed trait ExecutedOrder

object ExecutedOrder {

  @derive(decoder, encoder, loggable)
  final case class ExecutedOrderEvent(stringJson: String)

  @derive(decoder, encoder, loggable)
  final case class ExecutedRedeem(
    redeemCfg: Redeem,
    xReward: AssetAmount,
    yReward: AssetAmount,
    redeemOrderInputId: String,
    redeemUserOutputId: String,
    currPool: String,
    prevPoolId: String
  ) extends ExecutedOrder

  @derive(decoder, encoder, loggable)
  final case class ExecutedDeposit(
    depositCfg: Deposit,
    depositOrderInputId: String,
    depositUserOutputId: String,
    currPool: String,
    prevPoolId: String
  ) extends ExecutedOrder

  @derive(decoder, encoder, loggable)
  final case class ExecutedSwap(
    swapCfg: Swap,
    actualQuote: Amount,
    swapOrderInputId: String,
    swapUserOutputId: String,
    currPool: String,
    prevPoolId: String
  ) extends ExecutedOrder
}
