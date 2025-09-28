DROP TABLE IF EXISTS spot_rate;
CREATE TABLE spot_rate(
      id SERIAL NOT NULL,
      ccy1 varchar(3),
      ccy2 varchar(3),
      non_usd_ccy varchar(3),
      rate numeric(32,6),
      use_conversion numeric(32,6),
      snapshot_date timestamp,
      eod_flag varchar(1),
      updated_time timestamp,
      PRIMARY KEY (id)
);