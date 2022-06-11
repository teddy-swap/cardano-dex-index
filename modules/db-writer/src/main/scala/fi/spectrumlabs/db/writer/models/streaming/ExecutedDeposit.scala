package fi.spectrumlabs.db.writer.models.streaming

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fi.spectrumlabs.db.writer.models.orders._
import tofu.logging.derivation.loggable

@derive(decoder, encoder, loggable)
final case class ExecutedDeposit(
  config: Deposit,
  rewardLq: AssetEntry,
  orderInputId: BoxId,
  userOutputId: BoxId,
  poolOutputId: BoxId,
  poolInputId: BoxId
)
