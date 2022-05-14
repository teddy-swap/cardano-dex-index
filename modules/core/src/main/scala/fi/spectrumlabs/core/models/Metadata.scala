package fi.spectrumlabs.core.models

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fi.spectrumlabs.core.models.Bytea
import io.circe.Json

@derive(encoder, decoder)
final case class Metadata(key: BigInt, raw: Bytea, json: Json)
