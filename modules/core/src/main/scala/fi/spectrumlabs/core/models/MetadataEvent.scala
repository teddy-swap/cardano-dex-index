package fi.spectrumlabs.core.models

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fi.spectrumlabs.explorer.models.{Bytea, Metadata}
import io.circe.Json
import io.scalaland.chimney.dsl._

@derive(encoder, decoder)
final case class MetadataEvent(key: BigInt, raw: Bytea, json: Json)

object MetadataEvent {

  def fromExplorer(meta: Metadata): MetadataEvent =
    meta.into[MetadataEvent].transform
}
