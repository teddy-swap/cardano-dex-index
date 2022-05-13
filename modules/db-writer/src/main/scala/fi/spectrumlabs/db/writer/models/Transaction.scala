package fi.spectrumlabs.db.writer.models

import fi.spectrumlabs.core.models.models.{BlockHash, TxHash}
import fi.spectrumlabs.db.writer.classes.FromLedger
import io.circe.Json
import fi.spectrumlabs.core.models.{Transaction => Tx}
import io.circe.syntax._

final case class Transaction(
  blockHash: BlockHash,
  blockIndex: Long,
  hash: TxHash,
  invalidBefore: Option[BigInt],
  invalidHereafter: Option[BigInt],
  metadata: Option[Json],
  size: Int
)

object Transaction {

  implicit val fromLedger: FromLedger[Tx, Transaction] = (in: Tx) =>
    Transaction(
      in.blockHash,
      in.blockIndex,
      in.hash,
      in.invalidBefore,
      in.invalidHereafter,
      in.metadata.map(_.asJson),
      in.size
    )
}
