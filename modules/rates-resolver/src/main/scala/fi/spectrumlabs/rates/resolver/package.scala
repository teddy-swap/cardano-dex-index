package fi.spectrumlabs.rates

import fi.spectrumlabs.core.models.domain.AssetClass
import fi.spectrumlabs.rates.resolver.models.Metadata

package object resolver {
  final val AdaCMCId: 2010 = 2010
  final val UsdCMCId: 2781 = 2781

  final val AdaAssetClass   = AssetClass("", "")
  final val AdaDecimal: Int = 6
  final val DefaultDecimal  = AdaDecimal
  final val AdaMetadata     = Metadata(AdaDecimal, AdaAssetClass)
}
