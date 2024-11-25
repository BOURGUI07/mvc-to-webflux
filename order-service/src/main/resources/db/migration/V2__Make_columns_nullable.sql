-- V2 Migration: Make payment_id, inventory_id, and shipping_id columns nullable

-- Alter order_payment table
ALTER TABLE order_payment
    ALTER COLUMN payment_id DROP NOT NULL;

-- Alter order_inventory table
ALTER TABLE order_inventory
    ALTER COLUMN inventory_id DROP NOT NULL;

-- Alter order_shipping table
ALTER TABLE order_shipping
    ALTER COLUMN shipping_id DROP NOT NULL;
