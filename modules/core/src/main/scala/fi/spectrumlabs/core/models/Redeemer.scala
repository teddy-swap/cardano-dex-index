package fi.spectrumlabs.core.models

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import io.circe.Json

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
