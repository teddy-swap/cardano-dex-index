package fi.spectrumlabs.rates.resolver.models

import fi.spectrumlabs.core.models.domain.AssetClass

final case class Metadata(decimals: Int, asset: AssetClass)

object Metadata
