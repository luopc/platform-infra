DROP TABLE IF EXISTS spot_rate_eod;
CREATE TABLE spot_rate_eod(
      id SERIAL NOT NULL,
      ccy1 varchar(3),
      ccy2 varchar(3),
      non_usd_ccy varchar(3),
      rate numeric(32,6),
      use_conversion numeric(32,6),
      snapshot_date timestamp,
      eod_flag varchar(10),
      updated_time timestamp,
      PRIMARY KEY (id)
);