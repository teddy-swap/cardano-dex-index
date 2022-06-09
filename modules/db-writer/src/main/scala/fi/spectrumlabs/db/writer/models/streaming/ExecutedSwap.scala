package fi.spectrumlabs.db.writer.models.streaming

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fi.spectrumlabs.db.writer.models.orders.{Amount, Swap}
import tofu.logging.derivation.loggable

@derive(decoder, encoder, loggable)
final case class ExecutedSwap(
  swapCfg: Swap,
  actualQuote: Amount,
  swapOrderInputId: String,
  swapUserOutputId: String,
  currPool: String,
  prevPoolId: String
)
