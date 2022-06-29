package fi.spectrumlabs.core.models.domain

import cats.syntax.option._
import cats.syntax.show._
import cats.{Eq, Show}
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import doobie.util.Put
import tofu.logging.derivation.loggable

@derive(decoder, encoder, loggable)
final case class AssetClass(currencySymbol: String, tokenName: String)

object AssetClass {

  object syntax {
    implicit class AssetClassOps(val in: AssetClass) extends AnyVal {
      def toCoin: Coin = Coin(s"${in.currencySymbol}.${in.tokenName}")
    }
  }

  implicit val eq: Eq[AssetClass] = (x: AssetClass, y: AssetClass) =>
    x.tokenName == y.tokenName && x.currencySymbol == y.currencySymbol

  implicit val show: Show[AssetClass] = asset => s"${asset.currencySymbol}.${asset.tokenName}"

  implicit val put: Put[AssetClass] = implicitly[Put[String]].contramap(_.show)

  def fromString(in: String): Option[AssetClass] =
    in match {
      case "." => AssetClass("", "").some //todo fix insertion
      case str =>
        str.split('.').toList match {
          case cs :: tn :: Nil => AssetClass(cs, tn).some
          case _               => none
        }
    }

  def toMetadata(assetClass: AssetClass): String =
    s"${assetClass.currencySymbol}${assetClass.tokenName}"

}
