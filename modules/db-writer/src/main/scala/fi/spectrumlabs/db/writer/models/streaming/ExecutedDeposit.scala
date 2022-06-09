package fi.spectrumlabs.db.writer.models.streaming

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fi.spectrumlabs.db.writer.models.orders.{AssetEntry, Deposit}
import tofu.logging.derivation.loggable

@derive(decoder, encoder, loggable)
final case class ExecutedDeposit(
  depositCfg: Deposit,
  lqReward: AssetEntry,
  depositOrderInputId: String,
  depositUserOutputId: String,
  currPool: String,
  prevPoolId: String
)
