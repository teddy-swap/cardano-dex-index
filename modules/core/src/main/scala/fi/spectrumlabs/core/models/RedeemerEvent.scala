package fi.spectrumlabs.core.models

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fi.spectrumlabs.explorer.models.Redeemer
import fi.spectrumlabs.explorer.models._
import io.circe.Json
import io.scalaland.chimney.dsl._

@derive(encoder, decoder)
final case class RedeemerEvent(
  unitMem: Long,
  unitSteps: Long,
  fee: BigInt,
  purpose: ScriptPurpose,
  index: Int,
  scriptHash: Hash28,
  data: Option[Json],
  dataBin: Option[Bytea]
)

object RedeemerEvent {

  def fromExplorer(redeemer: Redeemer): RedeemerEvent =
    redeemer.into[RedeemerEvent].transform
}
