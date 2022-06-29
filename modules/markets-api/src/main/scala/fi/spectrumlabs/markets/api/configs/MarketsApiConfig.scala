package fi.spectrumlabs.markets.api.configs

import derevo.derive
import derevo.pureconfig.pureconfigReader
import tofu.logging.derivation.loggable

@derive(loggable, pureconfigReader)
final case class MarketsApiConfig(minLiquidityValue: Long)
