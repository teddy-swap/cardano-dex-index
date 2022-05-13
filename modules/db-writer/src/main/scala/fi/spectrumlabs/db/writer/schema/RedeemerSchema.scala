package fi.spectrumlabs.db.writer.schema

import fi.spectrumlabs.db.writer.models.Redeemer

class RedeemerSchema extends Schema[Redeemer] {
  val tableName: String = "redeemer"

  val fields: List[String] = List(
    "tx_hash",
    "tx_index",
    "unit_mem",
    "unit_step",
    "fee",
    "purpose",
    "index",
    "script_hash",
    "data",
    "data_bin"
  )
}
