package fi.spectrumlabs.db.writer.schema

import fi.spectrumlabs.db.writer.models.db.Swap

class ExecutedSwapSchema extends Schema[Swap] {
  val tableName: String = "swap"

  val fields: List[String] = List(
    "base",
    "quote",
    "pool_nft",
    "ex_fee_per_token_num",
    "ex_fee_per_token_den",
    "reward_pkh",
    "stake_pkh",
    "base_amount",
    "actual_quote",
    "min_quote_amount",
    "collateral_ada",
    "swap_order_input_id",
    "swap_user_output_id"
  )
}
