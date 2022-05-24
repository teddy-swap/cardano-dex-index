package fi.spectrumlabs.core.models

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fi.spectrumlabs.explorer.models.TxInput
import io.scalaland.chimney.dsl._

@derive(encoder, decoder)
final case class Input(
  out: Output,
  redeemer: Option[Redeemer]
)

object Input {

  def fromExplorer(in: TxInput): Input =
    in.into[Input].transform
}
