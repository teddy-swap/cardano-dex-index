package fi.spectrumlabs.core.models

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fi.spectrumlabs.core.models.models.{Asset32, PolicyId}

@derive(encoder, decoder)
final case class OutAsset(
  policyId: PolicyId,
  name: Asset32,
  quantity: BigInt,
  jsQuantity: String
)
