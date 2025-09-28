DROP TABLE IF EXISTS bank_quotation;
CREATE TABLE bank_quotation(
   id SERIAL NOT NULL,
   bank_code varchar(255),
   base_ccy varchar(3),
   quote_ccy varchar(3),
   exchange_sell numeric(24,8),
   exchange_buy numeric(24,8),
   cash_sell numeric(24,8),
   cash_buy numeric(24,8),
   middle numeric(24,8),
   updated_time timestamp,
   PRIMARY KEY (id)
);