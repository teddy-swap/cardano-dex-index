package fi.spectrumlabs.db.writer.schema

import fi.spectrumlabs.db.writer.models.Input

class InputSchema extends Schema[Input] {
  val tableName: String = "input"

  val fields: List[String] = List(
    "tx_hash",
    "tx_index",
    "out_ref",
    "out_index",
    "redeemer_index"
  )
}
