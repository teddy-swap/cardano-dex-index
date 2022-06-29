package fi.spectrumlabs.db.writer.models.orders

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fi.spectrumlabs.core.models.domain.{Amount, AssetClass}
import tofu.logging.derivation.{loggable, show}

@derive(decoder, encoder, show, loggable)
final case class Pool(
  id: AssetClass,
  reservesX: Amount,
  reservesY: Amount,
  liquidity: Amount,
  x: AssetClass,
  y: AssetClass,
  lq: AssetClass,
  fee: PoolFee,
  outCollateral: Amount
)
