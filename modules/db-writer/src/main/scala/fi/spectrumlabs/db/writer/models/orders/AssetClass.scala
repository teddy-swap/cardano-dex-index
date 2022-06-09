package fi.spectrumlabs.db.writer.models.orders

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import tofu.logging.derivation.loggable

@derive(decoder, encoder, loggable)
final case class AssetClass(currencySymbol: String, tokenName: String)

object AssetClass {

  object syntax {
    implicit class AssetClassOps(val in: AssetClass) extends AnyVal {
      def asName: String = s"${in.currencySymbol}.${in.tokenName}"
    }
  }
}
