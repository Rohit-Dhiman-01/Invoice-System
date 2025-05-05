create table if not exists customer
(
    id               bigint auto_increment
        primary key,
    billing_address  varchar(255),
    customer_name    varchar(255),
    email            varchar(255),
    gst_number       varchar(255),
    phone            varchar(255),
    shipping_address varchar(255)
);

create table if not exists invoice_number_generator
(
    year             int not null primary key,
    last_number_used int
);

create table if not exists purchase_order_number_generator
(
    year             int not null primary key,
    last_number_used int
);

create table if not exists quote
(
    id              bigint auto_increment
        primary key,
    created_at      datetime(6),
    last_updated_at datetime(6),
    currency        varchar(255),
    quote_date      date,
    quote_number    varchar(255),
    status          enum ('ACCEPTED', 'DRAFT', 'REJECTED', 'SENT'),
    sub_total       double,
    tax_amount      double,
    total_amount    double,
    valid_until     date,
    customer_id     bigint,
    constraint FKn1qsja9muj23doop1o72so357
        foreign key (customer_id) references customer (id)
);

create table if not exists item
(
    id          bigint auto_increment
        primary key,
    description varchar(255),
    hsn_code    varchar(255),
    item_name   varchar(255),
    quantity    int,
    rate        double,
    tax_percent double,
    total       double,
    quote_id    bigint,
    constraint FKlpjkgg5jaqo8oxfb8salsqswn
        foreign key (quote_id) references quote (id)
);

create table if not exists quote_number_generator
(
    year             int not null primary key,
    last_number_used int
);

create table if not exists vendor
(
    id          bigint auto_increment
        primary key,
    address     varchar(255),
    email       varchar(255),
    gst_number  varchar(255),
    phone       varchar(255),
    vendor_name varchar(255)
);

create table if not exists purchase_order
(
    id               bigint auto_increment
        primary key,
    created_at       datetime(6),
    last_updated_at  datetime(6),
    po_date          date,
    po_number        varchar(255),
    shipping_address varchar(255),
    status           enum ('APPROVED', 'CANCELLED', 'DRAFT'),
    sub_total        double,
    text_amount      double,
    total_amount     double,
    quote_id         bigint,
    vendor_id        bigint,
    constraint UKemt8ywvkg2qhyn2v5ndcjqs3h
        unique (quote_id),
    constraint FK20jcn7pw6hvx0uo0sh4y1d9xv
        foreign key (vendor_id) references vendor (id),
    constraint FKf9hqi1we2jr8to0brhvpafp2t
        foreign key (quote_id) references quote (id)
);

create table if not exists invoice
(
    id                bigint auto_increment
        primary key,
    created_at        datetime(6),
    last_updated_at   datetime(6),
    due_amount        double,
    due_date          date,
    invoice_date      date,
    invoice_number    varchar(255),
    payment_status    enum ('PAID', 'PARTIALLY_PAID', 'UNPAID'),
    sub_total         double,
    tax_amount        double,
    total_amount      double,
    customer_id       bigint,
    purchase_order_id bigint,
    constraint UK6wau8a22poajgbo68gmhgujys
        unique (purchase_order_id),
    constraint FK5e32ukwo9uknwhylogvta4po6
        foreign key (customer_id) references customer (id),
    constraint FKpbnhtmx9crcudpxcr5j2xjool
        foreign key (purchase_order_id) references purchase_order (id)
);

