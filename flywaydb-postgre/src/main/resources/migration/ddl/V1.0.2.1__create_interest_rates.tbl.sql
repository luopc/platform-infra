DROP TABLE IF EXISTS interest_rates;
CREATE TABLE interest_rates
(
    id             SERIAL      NOT NULL,
    country_code   VARCHAR(6)   NOT NULL,
    currency       VARCHAR(200) NOT NULL,
    rate_type      VARCHAR(50),
    tenor          VARCHAR(20),
    interest_rate  FLOAT8       NOT NULL,
    effective_date DATE,
    source_name    VARCHAR(50),
    created_at     TIMESTAMP,
    receive_time   TIMESTAMP,
    status         VARCHAR(20) DEFAULT 'ACTIVE',
    PRIMARY KEY (id)
);
COMMENT ON TABLE interest_rates IS 'interest_rates';
CREATE INDEX idx_interest_rates_country_tenor ON interest_rates(country_code, tenor);
CREATE INDEX idx_interest_rates_effective_date ON interest_rates(effective_date);
