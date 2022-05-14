package fi.spectrumlabs.core.models

import cats.syntax.either._
import doobie.util.{Get, Put}
import io.circe.{Decoder, Encoder}
import enumeratum._

sealed abstract class ScriptPurpose(override val entryName: String) extends EnumEntry

object ScriptPurpose extends Enum[ScriptPurpose] {
  val values = findValues

  case object Spend extends ScriptPurpose("spend")
  case object Mint extends ScriptPurpose("mint")
  case object Cert extends ScriptPurpose("cert")
  case object Reward extends ScriptPurpose("reward")

  implicit val encoder: Encoder[ScriptPurpose] = Encoder[String].contramap(_.entryName)

  implicit val decoder: Decoder[ScriptPurpose] =
    Decoder[String].emap(s => withNameInsensitiveEither(s).leftMap(_.getMessage()))

  implicit val put: Put[ScriptPurpose] = Put[String].contramap(_.entryName)
  implicit val get: Get[ScriptPurpose] = Get[String].temap(s => withNameInsensitiveEither(s).leftMap(_.getMessage()))

}
