package fi.spectrumlabs.db.writer.models.streaming

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fi.spectrumlabs.core.models.domain.{Amount, AssetAmount}
import fi.spectrumlabs.db.writer.models.orders.{Deposit, Redeem, Swap}
import tofu.logging.derivation.loggable

@derive(decoder, encoder, loggable)
sealed trait ExecutedOrderEvent

object ExecutedOrderEvent {

  @derive(decoder, encoder, loggable)
  final case class ExecutedDeposit(
    deposit: ExecutedOrder[Deposit],
    rewardLq: AssetAmount
  ) extends ExecutedOrderEvent

  @derive(decoder, encoder, loggable)
  final case class ExecutedRedeem(
    redeem: ExecutedOrder[Redeem],
    rewardX: AssetAmount,
    rewardY: AssetAmount
  ) extends ExecutedOrderEvent

  @derive(decoder, encoder, loggable)
  final case class ExecutedSwap(
    swap: ExecutedOrder[Swap],
    actualQuote: Amount
  ) extends ExecutedOrderEvent
}
