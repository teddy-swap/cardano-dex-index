package fi.spectrumlabs.db.writer.models

import fi.spectrumlabs.core.models.{BlockHash, TxHash}
import fi.spectrumlabs.db.writer.classes.FromLedger
import io.circe.Json
import fi.spectrumlabs.core.models.{Transaction => Tx}
import io.circe.syntax._

final case class Transaction(
  blockHash: BlockHash,
  blockIndex: Long,
  hash: TxHash,
  invalidBefore: Option[Long],
  invalidHereafter: Option[Long],
  metadata: Option[Json],
  size: Int
)

object Transaction {

  implicit val fromLedger: FromLedger[Tx, Transaction] = (in: Tx) =>
    Transaction(
      in.blockHash,
      in.blockIndex,
      in.hash,
      in.invalidBefore.map(_.toLong),
      in.invalidHereafter.map(_.toLong),
      in.metadata.map(_.asJson),
      in.size
  )
}
