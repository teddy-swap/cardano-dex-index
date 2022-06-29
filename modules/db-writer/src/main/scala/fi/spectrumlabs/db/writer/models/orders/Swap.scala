package fi.spectrumlabs.db.writer.models.orders

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fi.spectrumlabs.core.models.domain.{Amount, AssetClass}
import tofu.logging.derivation.{loggable, show}

@derive(decoder, encoder, loggable, show)
final case class Swap(
  poolId: AssetClass,
  baseIn: Amount,
  minQuoteOut: Amount,
  base: AssetClass,
  quote: AssetClass,
  exFee: ExFeePerToken,
  rewardPkh: PublicKeyHash,
  rewardSPkh: Option[StakePKH]
)
