package fi.spectrumlabs.db.writer.models.orders

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import tofu.logging.derivation.loggable

@derive(decoder, encoder, loggable)
final case class Swap(
  swapPoolId: PoolId,
  swapBaseIn: Amount,
  swapMinQuoteOut: Amount,
  swapBase: Coin,
  swapQuote: Coin,
  swapExFee: ExFeePerToken,
  swapRewardPkh: PublicKeyHash,
  swapRewardSPkh: Option[PublicKeyHash]
)
