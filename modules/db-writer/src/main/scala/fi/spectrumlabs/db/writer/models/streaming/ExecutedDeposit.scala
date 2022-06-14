package fi.spectrumlabs.db.writer.models.streaming

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fi.spectrumlabs.db.writer.models.orders._
import tofu.logging.derivation.loggable

@derive(decoder, encoder, loggable)
final case class ExecutedDeposit(
  deposit: ExecutedOrder[Deposit],
  rewardLq: AssetAmount
)
