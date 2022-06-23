package fi.spectrumlabs.markets.api.models

import derevo.derive
import tofu.logging.derivation.loggable

@derive(loggable)
final case class PoolVolume (xVolume: BigDecimal, yVolume: BigDecimal)
