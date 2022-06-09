package fi.spectrumlabs.db.writer.models.orders

import cats.Show
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import tofu.logging.Loggable

@derive(decoder, encoder)
final case class AssetEntry(value: (AssetClass, Amount))

object AssetEntry {
  implicit val show: Show[AssetEntry]         = _.toString
  implicit val loggable: Loggable[AssetEntry] = Loggable.show
}
