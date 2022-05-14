package fi.spectrumlabs.core.models

import cats.effect.Sync
import cats.syntax.either._
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fs2.kafka.{Deserializer, RecordDeserializer, RecordSerializer, Serializer}
import io.circe.parser.parse
import io.circe.syntax._

@derive(encoder, decoder)
final case class Transaction(
  blockHash: BlockHash,
  blockIndex: Long,
  globalIndex: Long,
  hash: TxHash,
  inputs: List[TxInput],
  outputs: List[TxOutput],
  invalidBefore: Option[BigInt],
  invalidHereafter: Option[BigInt],
  metadata: Option[Metadata],
  size: Int
)

object Transaction {
  implicit def recordSerializerTxn[F[_]: Sync]: RecordSerializer[F, Transaction] =
    RecordSerializer.lift(Serializer.string.contramap(_.asJson.noSpaces))

  implicit def recordDeserializerTxn[F[_]: Sync]: RecordDeserializer[F, Transaction] = RecordDeserializer.lift {
    Deserializer.string.map { str =>
      parse(str).flatMap(_.as[Transaction]).leftMap(throw _).merge
    }
  }
}
