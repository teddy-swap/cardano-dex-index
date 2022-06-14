create sequence if not exists executed_deposit_seq;
create sequence if not exists executed_swap_seq;
create sequence if not exists executed_redeem_seq;

create table if not exists executed_deposit (
    id Integer not null default nextval('executed_deposit_seq'),
    pool_nft Text not null,
    coin_x Text not null,
    coin_y Text not null,
    coin_lq Text not null,
    amount_x BIGINT not null,
    amount_y BIGINT not null,
    amount_lq BIGINT not null,
    ex_fee BIGINT not null,
    reward_pkh Text not null,
    stake_pkh Text,
    collateral_ada BIGINT not null,
    order_input_id Text not null,
    user_output_id Text not null,
    pool_input_Id Text not null,
    pool_output_Id Text not null
);

create table if not exists executed_redeem (
    id Integer not null default nextval('executed_redeem_seq'),
    pool_nft Text not null,
    coin_x Text not null,
    coin_y Text not null,
    coin_lq Text not null,
    amount_x BIGINT not null,
    amount_y BIGINT not null,
    amount_lq BIGINT not null,
    ex_fee BIGINT not null,
    reward_pkh Text not null,
    stake_pkh Text default null,
    order_input_id Text not null,
    user_output_id Text not null,
    pool_input_Id Text not null,
    pool_output_Id Text not null
);

create table if not exists executed_swap (
    id Integer not null default nextval('executed_swap_seq'),
    base Text not null,
    quote Text not null,
    pool_nft Text not null,
    ex_fee_per_token_num BIGINT not null,
    ex_fee_per_token_den BIGINT not null,
    reward_pkh Text not null,
    stake_pkh Text default null,
    base_amount BIGINT not null,
    actual_quote BIGINT not null,
    min_quote_amount BIGINT not null,
    order_input_id Text not null,
    user_output_id Text not null,
    pool_input_Id Text not null,
    pool_output_Id Text not null
);