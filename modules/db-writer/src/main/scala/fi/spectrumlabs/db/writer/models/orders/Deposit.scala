package fi.spectrumlabs.db.writer.models.orders

import cats.Show
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import tofu.logging.Loggable

@derive(decoder, encoder)
final case class Deposit(
  depositPoolId: PoolId,
  depositPair: (AssetEntry, AssetEntry),
  depositExFee: Long,
  depositRewardPkh: String,
  depositRewardSPkh: Option[String],
  adaCollateral: Long
)

object Deposit {
  implicit val show: Show[Deposit] = _.toString
  implicit val loggable: Loggable[Deposit] = Loggable.show
}