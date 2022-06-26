package fi.spectrumlabs.rates.resolver.models

import derevo.derive
import io.circe.Decoder
import tofu.logging.derivation.loggable

@derive(loggable)
final case class MetadataResponse(decimals: Int)

object MetadataResponse {

  implicit val decoder: Decoder[MetadataResponse] = Decoder.instance {
    _.downField("decimals").get[Int]("value").map(MetadataResponse(_))
  }
}
