package fi.spectrumlabs.db.writer.schema

import fi.spectrumlabs.db.writer.models.db.Pool

class PoolSchema extends Schema[Pool] {
  val tableName: String = "pool"

  val fields: List[String] = List(
    "pool_id",
    "reserves_x",
    "reserves_y",
    "liquidity",
    "x",
    "y",
    "lq",
    "pool_fee_num",
    "pool_fee_den",
    "out_collateral",
    "output_id",
    "timestamp"
  )
}
