package fi.spectrumlabs.db.writer.schema

import fi.spectrumlabs.db.writer.models.db.Deposit

class ExecutedDepositSchema extends Schema[Deposit] {
  val tableName: String = "deposit"

  val fields: List[String] = List(
    "pool_nft",
    "deposit_x",
    "deposit_y",
    "deposit_lq",
    "x_amount",
    "y_amount",
    "lq_amount",
    "ex_fee",
    "reward_pkh",
    "stake_pkh",
    "collateral_ada",
    "deposit_order_input_id",
    "deposit_user_output_id"
  )
}
