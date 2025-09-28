DROP TABLE IF EXISTS market_mock_data;
CREATE TABLE market_mock_data(
     symbol varchar(32) NOT NULL,
     trade_time timestamp,
     market_rv numeric(32,6),
     predict_rv numeric(32,6)
);