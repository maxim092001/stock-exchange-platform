CREATE TABLE stocks
(
    uuid        UUID PRIMARY KEY,
    token       VARCHAR UNIQUE NOT NULL,
    description VARCHAR        NOT NULL
);

CREATE INDEX stock_token_idx ON stocks (token);

CREATE TABLE users
(
    uuid     UUID PRIMARY KEY,
    name     VARCHAR UNIQUE NOT NULL,
    password VARCHAR        NOT NULL
);

CREATE TABLE user_stocks
(
		user_id  UUID NOT NULL,
		stock_id UUID NOT NULL,
		PRIMARY KEY (user_id, stock_id),
        CONSTRAINT user_stocks_fk_1
            FOREIGN KEY (user_id) REFERENCES users (uuid),
        CONSTRAINT user_stocks_fk_2
            FOREIGN KEY (stock_id) REFERENCES stocks (uuid)
)
