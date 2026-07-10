CREATE TABLE customers (
    id          VARCHAR(36)  NOT NULL,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    postal_code VARCHAR(10),
    prefecture  VARCHAR(50),
    city        VARCHAR(100),
    street      VARCHAR(255),
    created_at  TIMESTAMP    NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (email)
);

CREATE TABLE products (
    id              VARCHAR(36)    NOT NULL,
    name            VARCHAR(255)   NOT NULL,
    price_amount    DECIMAL(10, 2) NOT NULL,
    price_currency  VARCHAR(3)     NOT NULL DEFAULT 'JPY',
    description     TEXT,
    created_at      TIMESTAMP      NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE orders (
    id               VARCHAR(36)    NOT NULL,
    customer_id      VARCHAR(36)    NOT NULL,
    status           VARCHAR(20)    NOT NULL,
    total_amount     DECIMAL(10, 2) NOT NULL,
    total_currency   VARCHAR(3)     NOT NULL DEFAULT 'JPY',
    postal_code      VARCHAR(10),
    prefecture       VARCHAR(50),
    city             VARCHAR(100),
    street           VARCHAR(255),
    payment_id       VARCHAR(255),
    created_at       TIMESTAMP      NOT NULL,
    updated_at       TIMESTAMP      NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (customer_id) REFERENCES customers (id)
);

CREATE TABLE order_items (
    id                  VARCHAR(36)    NOT NULL,
    order_id            VARCHAR(36)    NOT NULL,
    product_id          VARCHAR(36)    NOT NULL,
    product_name        VARCHAR(255)   NOT NULL,
    unit_price_amount   DECIMAL(10, 2) NOT NULL,
    unit_price_currency VARCHAR(3)     NOT NULL DEFAULT 'JPY',
    quantity            INT            NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (order_id) REFERENCES orders (id)
);
