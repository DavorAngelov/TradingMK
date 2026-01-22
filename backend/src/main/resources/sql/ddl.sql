DROP TABLE IF EXISTS portfolio_holdings;
DROP TABLE IF EXISTS trade_request;
DROP TABLE IF EXISTS stock_history;
DROP TABLE IF EXISTS watchlist;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS oauth_pending_links;
DROP TABLE IF EXISTS user_auth_providers;
DROP TABLE IF EXISTS portfolios;
DROP TABLE IF EXISTS stock;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
   id BIGINT NOT NULL PRIMARY KEY,
   email VARCHAR(255) NOT NULL UNIQUE,
   password VARCHAR(255) NOT NULL,
   role VARCHAR(255) NOT NULL CHECK (role IN ('USER','ADMIN')),
   username VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS stock
(
    id BIGINT NOT NULL PRIMARY KEY,
    symbol VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    current_price DOUBLE PRECISION,
    last_price DOUBLE PRECISION,
    percentage DOUBLE PRECISION NOT NULL,
    turnover DOUBLE PRECISION,
    last_updated TIMESTAMP
);

CREATE TABLE IF NOT EXISTS portfolios
(
    id BIGINT NOT NULL PRIMARY KEY,
    balance NUMERIC(18,2) NOT NULL,
    user_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_portfolio_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS portfolio_holdings
(
    id BIGINT NOT NULL PRIMARY KEY,
    quantity INTEGER NOT NULL,
    avg_price NUMERIC(38,2) NOT NULL,
    stock_symbol VARCHAR(255) NOT NULL,
    portfolio_id BIGINT NOT NULL,
    CONSTRAINT fk_ph_portfolio FOREIGN KEY (portfolio_id) REFERENCES portfolios (id)
);

CREATE TABLE IF NOT EXISTS stock_history
(
    id BIGINT NOT NULL PRIMARY KEY,
    price DOUBLE PRECISION NOT NULL,
    symbol VARCHAR(255),
    timestamp DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS trade_request
(
    id BIGINT NOT NULL PRIMARY KEY,
    portfolio_id BIGINT,
    price_per_unit DOUBLE PRECISION NOT NULL,
    quantity INTEGER NOT NULL,
    status VARCHAR(255),
    stock_symbol VARCHAR(255),
    timestamp TIMESTAMP,
    type VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS transactions
(
    id BIGINT NOT NULL PRIMARY KEY,
    type VARCHAR(255),
    quantity INTEGER,
    price DOUBLE PRECISION,
    timestamp TIMESTAMP NOT NULL,
    origin VARCHAR(255) NOT NULL CHECK (origin IN ('INTERNAL','EXTERNAL')),
    user_id BIGINT,
    stock_id BIGINT,
    CONSTRAINT fk_tx_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_tx_stock FOREIGN KEY (stock_id) REFERENCES stock (id)
);

CREATE TABLE IF NOT EXISTS watchlist
(
    id BIGINT NOT NULL PRIMARY KEY,
    price_above DOUBLE PRECISION,
    price_below DOUBLE PRECISION,
    stock_id BIGINT,
    user_id BIGINT,
    CONSTRAINT fk_watch_stock FOREIGN KEY (stock_id) REFERENCES stock (id),
    CONSTRAINT fk_watch_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS user_auth_providers
(
    user_id BIGINT NOT NULL,
    auth_providers VARCHAR(255) NOT NULL CHECK (auth_providers IN ('INTERNAL','GOOGLE')),
    CONSTRAINT fk_auth_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS oauth_pending_links
(
    token VARCHAR(255) NOT NULL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    provider VARCHAR(255) NOT NULL CHECK (provider IN ('GOOGLE','INTERNAL')),
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL
);


    



