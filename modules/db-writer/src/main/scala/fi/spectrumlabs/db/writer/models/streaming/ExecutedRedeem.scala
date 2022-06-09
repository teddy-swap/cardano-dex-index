package fi.spectrumlabs.db.writer.models.streaming

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fi.spectrumlabs.db.writer.classes.FromLedger
import fi.spectrumlabs.db.writer.models.orders.{AssetAmount, Redeem}
import io.circe.parser.parse
import tofu.logging.derivation.loggable

@derive(decoder, encoder, loggable)
final case class ExecutedRedeem(
  redeemCfg: Redeem,
  xReward: AssetAmount,
  yReward: AssetAmount,
  redeemOrderInputId: String,
  redeemUserOutputId: String,
  currPool: String,
  prevPoolId: String
)

object ExecutedRedeem {

  implicit val fromLedger: FromLedger[ExecutedOrderEvent, Option[ExecutedRedeem]] =
    (in: ExecutedOrderEvent) => parse(in.stringJson).toOption.flatMap(_.as[ExecutedRedeem].toOption)
}
