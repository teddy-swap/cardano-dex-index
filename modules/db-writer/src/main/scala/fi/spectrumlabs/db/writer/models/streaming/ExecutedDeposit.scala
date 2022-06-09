package fi.spectrumlabs.db.writer.models.streaming

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fi.spectrumlabs.db.writer.classes.FromLedger
import fi.spectrumlabs.db.writer.models.orders.Deposit
import io.circe.parser.parse
import tofu.logging.derivation.loggable

@derive(decoder, encoder, loggable)
final case class ExecutedDeposit( //todo add actual lq reward, lqName
  depositCfg: Deposit,
  depositOrderInputId: String,
  depositUserOutputId: String,
  currPool: String,
  prevPoolId: String
)

object ExecutedDeposit {}
