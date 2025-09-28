DROP TABLE IF EXISTS currency_mapping;
CREATE TABLE currency_mapping(
     id SERIAL NOT NULL,
     ccy varchar(3),
     ccy_num integer,
     currency_name varchar(90),
     country_name varchar(90),
     delivery varchar(1),
     updated_time timestamp,
     PRIMARY KEY (id)
);