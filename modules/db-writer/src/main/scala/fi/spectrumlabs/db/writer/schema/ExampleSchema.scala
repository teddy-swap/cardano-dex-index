package fi.spectrumlabs.db.writer.schema

import fi.spectrumlabs.db.writer.models.ExampleData

final class ExampleSchema extends Schema[ExampleData] {
  val tableName: String    = "example_db_name"
  val fields: List[String] = List("blockHash", "blockIndex", "globalIndex")
}
