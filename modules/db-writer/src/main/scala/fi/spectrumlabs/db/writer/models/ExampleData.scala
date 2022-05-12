package fi.spectrumlabs.db.writer.models

import doobie.Put
import fi.spectrumlabs.core.models.models.BlockHash

final case class ExampleData(blockHash: BlockHash, blockIndex: Long, globalIndex: Long)

object ExampleData {
  implicit val put: Put[ExampleData] = ???
}
