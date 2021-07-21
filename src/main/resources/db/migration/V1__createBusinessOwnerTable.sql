create table business_owner (
    bo_number bigint primary key not null,
    first_name varchar(50) not null,
    last_name varchar(50) not null,
    address varchar(1024) not null
);