package fi.spectrumlabs.db.writer.models.streaming

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fi.spectrumlabs.db.writer.classes.FromLedger
import fi.spectrumlabs.db.writer.models.orders.{Amount, Swap}
import io.circe.parser.parse
import tofu.logging.derivation.loggable

@derive(decoder, encoder, loggable)
final case class ExecutedSwap(
  swapCfg: Swap,
  actualQuote: Amount,
  swapOrderInputId: String,
  swapUserOutputId: String,
  currPool: String,
  prevPoolId: String
)

object ExecutedSwap {

  implicit val fromLedger: FromLedger[ExecutedOrderEvent, Option[ExecutedSwap]] =
    (in: ExecutedOrderEvent) => parse(in.stringJson).toOption.flatMap(_.as[ExecutedSwap].toOption)
}
