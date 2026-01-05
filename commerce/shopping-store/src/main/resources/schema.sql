DROP TABLE IF EXISTS product CASCADE;

CREATE TABLE IF NOT EXISTS product (
    product_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    image_src VARCHAR(255),
    quantity_state VARCHAR(20) NOT NULL,
    product_state VARCHAR(20) NOT NULL,
    product_category VARCHAR(20) NOT NULL,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 1)
);