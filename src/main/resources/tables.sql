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