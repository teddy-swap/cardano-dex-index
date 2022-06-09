package fi.spectrumlabs.db.writer.models

import doobie.{Get, Put}
import fi.spectrumlabs.explorer.models.TxHash
import io.circe.{Decoder, Encoder}
import io.estatico.newtype.macros.newtype
import tofu.logging.Loggable

package object orders {

  @newtype final case class Amount(value: Long)

  object Amount {
    implicit val encoder: Encoder[Amount]   = deriving
    implicit val decoder: Decoder[Amount]   = deriving
    implicit val loggable: Loggable[Amount] = deriving
    implicit val get: Get[Amount]           = deriving
    implicit val put: Put[Amount]           = deriving
  }

  @newtype final case class PoolId(value: String)

  object PoolId {
    implicit val encoder: Encoder[PoolId]   = deriving
    implicit val decoder: Decoder[PoolId]   = deriving
    implicit val loggable: Loggable[PoolId] = deriving
    implicit val get: Get[PoolId]           = deriving
    implicit val put: Put[PoolId]           = deriving
  }

  @newtype final case class Coin(value: String)

  object Coin {
    implicit val encoder: Encoder[Coin]   = deriving
    implicit val decoder: Decoder[Coin]   = deriving
    implicit val loggable: Loggable[Coin] = deriving
    implicit val get: Get[Coin]           = deriving
    implicit val put: Put[Coin]           = deriving
  }
}
