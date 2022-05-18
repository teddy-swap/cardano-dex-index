package fi.spectrumlabs.core.models

import cats.data.NonEmptyList
import cats.effect.Sync
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fs2.kafka.{Deserializer, RecordDeserializer, RecordSerializer, Serializer}
import io.circe.parser.parse
import io.circe.syntax._

sealed trait TxModel {
  val hash: TxHash
}

@derive(encoder, decoder)
final case class Transaction(
  blockHash: BlockHash,
  blockIndex: Long,
  globalIndex: Long,
  hash: TxHash,
  inputs: NonEmptyList[TxInput],
  outputs: NonEmptyList[TxOutput],
  invalidBefore: Option[BigInt],
  invalidHereafter: Option[BigInt],
  metadata: Option[Metadata],
  size: Int
) extends TxModel

object Transaction {

  implicit def recordSerializerTxn[F[_]: Sync]: RecordSerializer[F, Transaction] =
    RecordSerializer.lift(Serializer.string.contramap(_.asJson.noSpaces))

  implicit def recordDeserializerTxn[F[_]: Sync]: RecordDeserializer[F, Option[Transaction]] = RecordDeserializer.lift {
    Deserializer.string.map { str =>
      parse(str).flatMap(_.as[Transaction])
        .toOption
    }
  }
}

@derive(encoder, decoder)
final case class LegacyTx(hash: TxHash) extends TxModel
