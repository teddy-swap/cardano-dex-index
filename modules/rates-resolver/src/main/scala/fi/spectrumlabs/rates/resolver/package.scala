package fi.spectrumlabs.rates

import fi.spectrumlabs.core.{AdaAssetClass, AdaDecimal}
import fi.spectrumlabs.rates.resolver.models.Metadata

package object resolver {
  final val AdaCMCId: 2010 = 2010
  final val UsdCMCId: 2781 = 2781

  final val AdaMetadata = Metadata(AdaDecimal, AdaAssetClass)

}
