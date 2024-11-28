ALTER TABLE products
    ADD COLUMN created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    ADD COLUMN updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL;

ALTER TABLE product_inventory
    ADD COLUMN created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    ADD COLUMN updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL;