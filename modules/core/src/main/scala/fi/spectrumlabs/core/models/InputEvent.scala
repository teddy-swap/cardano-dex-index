package fi.spectrumlabs.core.models

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fi.spectrumlabs.explorer.models.TxInput
import io.scalaland.chimney.dsl._

@derive(encoder, decoder)
final case class InputEvent(
  out: OutputEvent,
  redeemer: Option[RedeemerEvent]
)

object InputEvent {

  def fromExplorer(in: TxInput): InputEvent =
    in.into[InputEvent].transform
}
