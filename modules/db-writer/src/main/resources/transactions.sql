create sequence txn_id_seq;
create sequence output_id_seq;
create sequence input_id_seq;
create sequence redeemer_id_seq;

create table if not exists transaction (
    id Integer not null default nextval('txn_id_seq'),
    block_hash Text not null,
    block_index Integer not null,
    global_index Integer not null,
    hash Text not null,
    invalid_before BIGINT default null,
    invalid_hereafter BIGINT default null,
    metadata Jsonb default null,
    size Integer not null,
    primary key (id)
);

create table if not exists output (
    id Integer not null default nextval('output_id_seq'),
    tx_id Integer not null references transaction (id),
    ref Text not null,
    block_hash Text not null,
    tx_hash Text not null,
    index Integer not null,
    global_index Integer not null,
    addr Text not null,
    raw_addr Text not null,
    payment_cred Text default null,
    value Jsonb,
    data_hash Text default null,
    data Jsonb default null,
    data_bin Text default null,
    spent_by_tx_hash Text default null,
    primary key (id)
);

create table if not exists input (
    id Integer not null default nextval('input_id_seq'),
    tx_id Integer not null references transaction (id),
    tx_out_id Integer not null references output (id),
    primary key (id)
);

create table if not exists redeemer (
    id Integer not null default nextval('redeemer_id_seq'),
    tx_in_id Integer not null references input (id),
    unit_mem Integer not null,
    unit_step Integer not null,
    fee Integer not null,
    purpose Text not null,
    index Integer not null,
    script_hash Text not null,
    data Jsonb default null,
    data_bin Text default null,
    primary key (id)
);

create unique index tx_id on transaction (id);

create unique index tx_out_tx_id on output (tx_id);
create unique index tx_out_id on output (id);

create unique index tx_in_tx_id on input (tx_id);
create unique index tx_in_id on input (id);

create unique index redeemer_tx_in_id on redeemer (tx_in_id);
create unique index redeemer_id on redeemer (id);

create unique index tx_out_pcred on output (payment_cred);
