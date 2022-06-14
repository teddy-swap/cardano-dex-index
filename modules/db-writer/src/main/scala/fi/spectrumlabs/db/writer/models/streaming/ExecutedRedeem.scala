package fi.spectrumlabs.db.writer.models.streaming

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fi.spectrumlabs.db.writer.classes.FromLedger
import fi.spectrumlabs.db.writer.models.orders._
import io.circe.parser.parse
import tofu.logging.derivation.loggable

@derive(decoder, encoder, loggable)
final case class ExecutedRedeem(
  redeem: ExecutedOrder[Redeem],
  rewardX: AssetAmount,
  rewardY: AssetAmount
)
