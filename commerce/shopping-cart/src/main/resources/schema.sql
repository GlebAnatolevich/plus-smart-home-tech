DROP TABLE IF EXISTS cart CASCADE;
DROP TABLE IF EXISTS cart_product CASCADE;

CREATE TABLE IF NOT EXISTS cart (
    shopping_cart_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username varchar(255) NOT NULL,
    status varchar(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS cart_product (
    cart_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity BIGINT,
    CONSTRAINT fk_cart_product_cart
        FOREIGN KEY (cart_id) REFERENCES cart(shopping_cart_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);