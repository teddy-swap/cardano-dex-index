package fi.spectrumlabs.db.writer.schema

import fi.spectrumlabs.db.writer.models.Transaction

class TransactionSchema extends Schema[Transaction] {
  val tableName: String = "transaction"

  val fields: List[String] = List(
    "block_hash",
    "block_index",
    "hash",
    "invalid_before",
    "invalid_hereafter",
    "metadata",
    "size"
  )
}
