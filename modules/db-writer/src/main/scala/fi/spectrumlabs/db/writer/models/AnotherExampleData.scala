package fi.spectrumlabs.db.writer.models

import doobie.Put
import fi.spectrumlabs.core.models.models.TxHash

final case class AnotherExampleData(hash: TxHash, globalIndex: Long)

object AnotherExampleData {
  implicit val put: Put[AnotherExampleData] = ???
}