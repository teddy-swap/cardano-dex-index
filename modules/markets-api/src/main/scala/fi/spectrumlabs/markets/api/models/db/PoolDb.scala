package fi.spectrumlabs.markets.api.models.db

import derevo.derive
import tofu.logging.derivation.loggable

@derive(loggable)
final case class PoolDb(
  poolId: String,
  x: String,
  xReserves: Long,
  y: String,
  yReserves: Long,
  feeNum: BigDecimal,
  feeDen: BigDecimal
)
