package fi.spectrumlabs.models

import cats.effect.Sync
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import fs2.kafka.{RecordSerializer, Serializer}
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
}