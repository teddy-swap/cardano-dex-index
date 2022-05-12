package fi.spectrumlabs.db.writer.schema

import fi.spectrumlabs.db.writer.models.AnotherExampleData

final class AnotherExampleSchema extends Schema[AnotherExampleData] {
  val tableName: String    = "another_example_db"
  val fields: List[String] = List("hash", "globalIndex")
}
