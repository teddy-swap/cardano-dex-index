package fi.spectrumlabs.core.models

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fi.spectrumlabs.explorer.models.{Redeemer => R}
import fi.spectrumlabs.explorer.models._
import io.circe.Json
import io.scalaland.chimney.dsl._

@derive(encoder, decoder)
final case class Redeemer(
  unitMem: Long,
  unitSteps: Long,
  fee: BigInt,
  purpose: ScriptPurpose,
  index: Int,
  scriptHash: Hash28,
  data: Option[Json],
  dataBin: Option[Bytea]
)

object Redeemer {

  def fromExplorer(redeemer: R): Redeemer =
    redeemer.into[Redeemer].transform
}
