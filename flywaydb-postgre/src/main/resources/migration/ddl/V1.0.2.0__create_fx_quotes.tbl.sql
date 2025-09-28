DROP TABLE IF EXISTS fx_quotes;
CREATE TABLE fx_quotes
(
    id            SERIAL         NOT NULL,
    currency_pair VARCHAR(10)  NOT NULL,
    bid_price     FLOAT8       NOT NULL,
    ask_price     FLOAT8       NOT NULL,
    mid_price     FLOAT8       NOT NULL,
    source_name   VARCHAR(100) NOT NULL,
    quote_time    TIMESTAMP    NOT NULL,
    received_time TIMESTAMP    NOT NULL,
    status        VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    raw_data      TEXT,
    PRIMARY KEY (id)
);
COMMENT ON TABLE fx_quotes IS 'fx_quotes';
CREATE INDEX idx_fx_quotes_pair_time ON fx_quotes(currency_pair, quote_time);
CREATE INDEX idx_fx_quotes_source_time ON fx_quotes(source_name, quote_time);
