create table "order"
(
    id               bigint generated by default as identity
        primary key,
    order_id         bigint                   not null,
    total_amount     integer                  not null,
    date_order       timestamp with time zone not null,
    recipient        varchar(255)             not null,
    address_delivery varchar(255)             not null,
    payment_type     varchar(255)             not null,
    delivery_type    varchar(255)             not null
);

create table order_detail
(
    id              bigint generated by default as identity
        primary key,
    article_id      bigint       not null,
    item_name       varchar(255) not null,
    quantity        integer      not null,
    amount_per_item integer      not null,
    order_id        bigint       not null
        constraint order_detail_fkey_order
            references "order"
);
