DROP TABLE IF EXISTS market_quotation;
CREATE TABLE market_quotation(
     id SERIAL NOT NULL,
     base_ccy varchar(3),
     quote_ccy varchar(3),
     buy numeric(24,8),
     sell numeric(24,8),
     last_prices varchar(900),
     PRIMARY KEY (id)
);