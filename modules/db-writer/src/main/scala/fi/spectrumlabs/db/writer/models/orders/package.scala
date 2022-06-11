package fi.spectrumlabs.db.writer.models

import doobie.{Get, Put}
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

  @newtype final case class ExFee(value: Long)

  object ExFee {
    implicit val encoder: Encoder[ExFee]   = deriving
    implicit val decoder: Decoder[ExFee]   = deriving
    implicit val loggable: Loggable[ExFee] = deriving
    implicit val get: Get[ExFee]           = deriving
    implicit val put: Put[ExFee]           = deriving
  }

  @newtype final case class BoxId(value: String)

  object BoxId {
    implicit val encoder: Encoder[BoxId]   = deriving
    implicit val decoder: Decoder[BoxId]   = deriving
    implicit val loggable: Loggable[BoxId] = deriving
    implicit val get: Get[BoxId]           = deriving
    implicit val put: Put[BoxId]           = deriving
  }

  @newtype final case class PublicKeyHash(value: String)

  object PublicKeyHash {
    implicit val encoder: Encoder[PublicKeyHash]   = deriving
    implicit val decoder: Decoder[PublicKeyHash]   = deriving
    implicit val loggable: Loggable[PublicKeyHash] = deriving
    implicit val get: Get[PublicKeyHash]           = deriving
    implicit val put: Put[PublicKeyHash]           = deriving
  }

  @newtype final case class CollateralAda(value: Long)

  object CollateralAda {
    implicit val encoder: Encoder[CollateralAda]   = deriving
    implicit val decoder: Decoder[CollateralAda]   = deriving
    implicit val loggable: Loggable[CollateralAda] = deriving
    implicit val get: Get[CollateralAda]           = deriving
    implicit val put: Put[CollateralAda]           = deriving
  }
}
