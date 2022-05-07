package fi.spectrumlabs.models

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive

@derive(encoder, decoder)
final case class TxInput(
  out: TxOutput,
  redeemer: Option[Redeemer]
)
