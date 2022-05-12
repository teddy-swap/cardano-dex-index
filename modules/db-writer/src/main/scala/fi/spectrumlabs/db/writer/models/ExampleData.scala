package fi.spectrumlabs.db.writer.models

import doobie.Put
import fi.spectrumlabs.core.models.Transaction
import fi.spectrumlabs.core.models.models.BlockHash
import fi.spectrumlabs.db.writer.classes.FromLedger

final case class ExampleData(blockHash: BlockHash, blockIndex: Long, globalIndex: Long)

object ExampleData {
  implicit val fromLedger: FromLedger[Transaction, ExampleData] =
    (in: Transaction) => ExampleData(in.blockHash, in.blockIndex, in.globalIndex)

  implicit val put: Put[ExampleData] = ???
}
