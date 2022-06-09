create table if not exists deposit (
     pool_nft Text,
     deposit_x Text,
     deposit_y Text,
     deposit_lq Text,
     x_amount int,
     y_amount int,
     lq_amount int,
     ex_fee int,
     reward_pkh Text,
     stake_pkh Text,
     collateral_ada int,
     deposit_order_input_id Text,
     deposit_user_output_id Text
);

create table if not exists redeem (
     pool_nft Text,
     redeem_x Text,
     redeem_y Text,
     redeem_lq Text,
     x_amount int,
     y_amount int,
     lq_amount int,
     ex_fee int,
     reward_pkh Text,
     stake_pkh Text,
     redeem_order_input_id Text,
     redeem_user_output_id Text
);

create table if not exists swap (
     base Text,
     quote Text,
     pool_nft Text,
     ex_fee_per_token_num int,
     ex_fee_per_token_den int,
     reward_pkh Text,
     stake_pkh Text,
     base_amount int,
     actual_quote: int,
     min_quote_amount int,
     swap_order_input_id Text,
     swap_user_output_id Text
);