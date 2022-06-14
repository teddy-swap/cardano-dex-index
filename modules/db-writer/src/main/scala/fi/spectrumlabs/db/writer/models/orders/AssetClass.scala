package fi.spectrumlabs.db.writer.models.orders

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import doobie.{Get, Meta}
import tofu.logging.derivation.{loggable, show}

@derive(decoder, encoder, loggable, show)
final case class AssetClass(currencySymbol: String, tokenName: String)

object AssetClass {

  object syntax {
    implicit class AssetClassOps(val in: AssetClass) extends AnyVal {
      def toCoin: Coin = Coin(s"${in.currencySymbol}.${in.tokenName}")
    }
  }
}
