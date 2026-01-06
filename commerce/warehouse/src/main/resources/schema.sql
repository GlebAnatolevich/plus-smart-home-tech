DROP TABLE IF EXISTS product_storage CASCADE;

CREATE TABLE IF NOT EXISTS product_storage (
    product_id UUID NOT NULL UNIQUE PRIMARY KEY,

    fragile BOOLEAN,

    width DECIMAL(10, 3) NOT NULL,
    height DECIMAL(10, 3) NOT NULL,
    depth DECIMAL(10, 3) NOT NULL,

    weight DECIMAL(10, 3) NOT NULL,
    quantity BIGINT NOT NULL DEFAULT 0
);