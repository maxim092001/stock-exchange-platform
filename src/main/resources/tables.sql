CREATE TABLE stocks
(
    uuid        UUID PRIMARY KEY,
    ticker      VARCHAR UNIQUE NOT NULL,
    description VARCHAR        NOT NULL
);

CREATE INDEX stock_ticker_idx ON stocks (ticker);

CREATE TABLE users
(
    uuid     UUID PRIMARY KEY,
    name     VARCHAR UNIQUE NOT NULL,
    password VARCHAR        NOT NULL
);

CREATE TABLE user_stocks
(
		user_id  UUID NOT NULL,
		ticker   VARCHAR NOT NULL,
		PRIMARY KEY (user_id, ticker),
        CONSTRAINT user_stocks_fk_1
            FOREIGN KEY (user_id) REFERENCES users (uuid),
        CONSTRAINT user_stocks_fk_2
            FOREIGN KEY (ticker) REFERENCES stocks (ticker)
);