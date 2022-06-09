package fi.spectrumlabs.db.writer.schema

import fi.spectrumlabs.db.writer.models.db.Redeem

class ExecutedRedeemSchema extends Schema[Redeem] {
  val tableName: String = "redeem"

  val fields: List[String] = List(
    "pool_nft",
    "redeem_x",
    "redeem_y",
    "redeem_lq",
    "x_amount",
    "y_amount",
    "lq_amount",
    "ex_fee",
    "reward_pkh",
    "stake_pkh",
    "redeem_order_input_id",
    "redeem_user_output_id"
  )
}
