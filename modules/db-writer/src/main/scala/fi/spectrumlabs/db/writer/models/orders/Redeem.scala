package fi.spectrumlabs.db.writer.models.orders

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import tofu.logging.derivation.loggable

@derive(decoder, encoder, loggable)
final case class Redeem(
  redeemPoolId: PoolId,
  redeemLqIn: Amount,
  redeemLq: Coin,
  redeemExFee: ExFee,
  redeemRewardPkh: PublicKeyHash,
  redeemRewardSPkh: Option[PublicKeyHash]
)
