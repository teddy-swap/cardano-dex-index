package fi.spectrumlabs.core.models

import cats.Show
import doobie.util.{Get, Put}
import io.circe.{Decoder, Encoder}
import io.estatico.newtype.macros.newtype
import tofu.logging.Loggable

package object domain {
  @newtype final case class PoolId(value: String)

  object PoolId {
    implicit val loggable: Loggable[PoolId] = deriving
    implicit val get: Get[PoolId]           = deriving
    implicit val put: Put[PoolId]           = deriving
  }

  @newtype final case class Amount(value: Long)

  object Amount {
    implicit val encoder: Encoder[Amount]   = deriving
    implicit val decoder: Decoder[Amount]   = deriving
    implicit val loggable: Loggable[Amount] = deriving
    implicit val get: Get[Amount]           = deriving
    implicit val put: Put[Amount]           = deriving
    implicit val show: Show[Amount]         = deriving
  }

  @newtype final case class Coin(value: String)

  object Coin {
    implicit val encoder: Encoder[Coin]   = deriving
    implicit val decoder: Decoder[Coin]   = deriving
    implicit val loggable: Loggable[Coin] = deriving
    implicit val get: Get[Coin]           = deriving
    implicit val put: Put[Coin]           = deriving
    implicit val show: Show[Coin]         = deriving
  }
}
