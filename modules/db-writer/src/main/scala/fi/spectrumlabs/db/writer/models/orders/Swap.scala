package fi.spectrumlabs.db.writer.models.orders

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import tofu.logging.derivation.loggable

//todo: add newtypes for all values

@derive(decoder, encoder, loggable)
final case class Swap(
  swapPoolId: PoolId,
  swapBaseIn: Amount,
  swapMinQuoteOut: Amount,
  swapBase: Coin,
  swapQuote: Coin,
  swapExFee: ExFeePerToken,
  swapRewardPkh: String,
  swapRewardSPkh: Option[String]
)
