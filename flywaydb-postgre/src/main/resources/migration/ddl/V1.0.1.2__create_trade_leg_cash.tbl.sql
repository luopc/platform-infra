DROP TABLE IF EXISTS trade_leg_cash;
CREATE TABLE trade_leg_cash(
    leg_id varchar(32) NOT NULL,
    trade_id varchar(32) NOT NULL,
    version_id integer NOT NULL,
    leg_role integer NOT NULL,
    pri_ccy varchar(3) NOT NULL,
    pri_amt numeric(32,6) NOT NULL,
    cnt_ccy varchar(3) NOT NULL,
    cnt_amt numeric(32,6) NOT NULL,
    dealt_rate numeric(24,8) NOT NULL,
    value_date timestamp NOT NULL,
    ccy_pair varchar(10),
    notional_amt numeric(32,6),
    side_type varchar(10),
    ndf_fixing_amt numeric(32,6),
    ndf_fixing_rate numeric(10,6),
    ndf_fixing_source varchar(255),
    ndf_fixing_date timestamp,
    uti varchar(255),
    regulatory_key varchar(255),
    down_stream_id varchar(255),
    PRIMARY KEY (leg_id)
);
