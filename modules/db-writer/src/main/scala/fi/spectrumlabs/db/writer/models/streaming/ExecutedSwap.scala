package fi.spectrumlabs.db.writer.models.streaming

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fi.spectrumlabs.db.writer.models.orders._
import tofu.logging.derivation.loggable

@derive(decoder, encoder, loggable)
final case class ExecutedSwap(
  config: Swap,
  actualQuote: Amount,
  orderInputId: BoxId,
  userOutputId: BoxId,
  poolOutputId: BoxId,
  poolInputId: BoxId
)
