DROP TABLE IF EXISTS ccy_interest;
CREATE TABLE ccy_interest(
     ccy varchar(3) NOT NULL,
     country_name varchar(90),
     rate numeric(24,8),
     pre_rate numeric(24,8),
     updated_time timestamp,
     PRIMARY KEY (ccy)
);