package fi.spectrumlabs.db.writer.schema

import fi.spectrumlabs.db.writer.models.Output

class OutputSchema extends Schema[Output] {
  val tableName: String = "output"

  val fields: List[String] = List(
    "tx_hash",
    "tx_index",
    "ref",
    "block_hash",
    "index",
    "addr",
    "raw_addr",
    "payment_cred",
    "value",
    "data_hash",
    "data",
    "data_bin",
    "spent_by_tx_hash"
  )
}
