DROP TABLE IF EXISTS purchase_order;
DROP TABLE IF EXISTS order_inventory;
DROP TABLE IF EXISTS order_payment;
DROP TABLE IF EXISTS order_shipping;
DROP TABLE IF EXISTS product;


-- Create Product table
CREATE TABLE product (
                          id BIGSERIAL PRIMARY KEY,
                          product_id BIGINT NOT NULL UNIQUE,
                          code VARCHAR(255) NOT NULL,
                          price DECIMAL(19,2) NOT NULL
);

-- Create Purchase Order table
CREATE TABLE purchase_order (
                                 order_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
                                 product_id BIGINT NOT NULL,
                                 customer_id BIGINT NOT NULL,
                                 quantity INTEGER NOT NULL,
                                 price DECIMAL(19,2) NOT NULL,
                                 status TEXT NOT NULL,
                                 amount DECIMAL(19,2) NOT NULL,
                                 version INTEGER NOT NULL DEFAULT 0,
                                 FOREIGN KEY (product_id) REFERENCES product(product_id)
);

-- Create Order Inventory table
CREATE TABLE order_inventory (
                                   id BIGSERIAL PRIMARY KEY,
                                   order_id UUID NOT NULL,
                                   inventory_id UUID NOT NULL,
                                   message TEXT,
                                   status TEXT NOT NULL,
                                   success BOOLEAN NOT NULL,
                                   FOREIGN KEY (order_id) REFERENCES purchase_order(order_id)
);

-- Create Order Payment table
CREATE TABLE order_payment (
                                id BIGSERIAL PRIMARY KEY,
                                order_id UUID NOT NULL,
                                payment_id UUID NOT NULL,
                                message TEXT,
                                status TEXT NOT NULL,
                                success BOOLEAN NOT NULL,
                                FOREIGN KEY (order_id) REFERENCES purchase_order(order_id)
);

-- Create Order Shipping table
CREATE TABLE order_shipping (
                                 id BIGSERIAL PRIMARY KEY,
                                 order_id UUID NOT NULL,
                                 shipping_id UUID NOT NULL,
                                 message TEXT,
                                 status TEXT NOT NULL,
                                 success BOOLEAN NOT NULL,
                                 delivery_date TIMESTAMP,
                                 FOREIGN KEY (order_id) REFERENCES purchase_order(order_id)
);