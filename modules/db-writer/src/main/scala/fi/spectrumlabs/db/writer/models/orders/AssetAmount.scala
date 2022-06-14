package fi.spectrumlabs.db.writer.models.orders

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import tofu.logging.derivation.{loggable, show}

@derive(decoder, encoder, loggable, show)
final case class AssetAmount(asset: AssetClass, amount: Amount)
