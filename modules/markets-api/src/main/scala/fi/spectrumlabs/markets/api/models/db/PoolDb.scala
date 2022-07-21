package fi.spectrumlabs.markets.api.models.db

import derevo.derive
import fi.spectrumlabs.core.models.domain.AssetClass
import tofu.logging.derivation.loggable

@derive(loggable)
final case class PoolDb(
  poolId: String,
  x: AssetClass,
  xReserves: Long,
  y: AssetClass,
  yReserves: Long,
  feeNum: BigDecimal,
  feeDen: BigDecimal
)
