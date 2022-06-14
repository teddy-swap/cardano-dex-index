package fi.spectrumlabs.db.writer.models.orders

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import tofu.logging.derivation.{loggable, show}

@derive(decoder, encoder, loggable, show)
final case class Redeem(
  poolId: AssetClass,
  lqIn: Amount,
  lq: AssetClass,
  exFee: ExFee,
  rewardPkh: PublicKeyHash,
  rewardSPkh: Option[StakePKH]
)
