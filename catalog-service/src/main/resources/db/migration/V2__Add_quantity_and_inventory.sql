-- Add the quantity field to the products table with a default value
ALTER TABLE products
    ADD COLUMN available_quantity INTEGER DEFAULT 10 NOT NULL;

-- Update the sample data in products to include quantities ranging from 10 to 100 copies
UPDATE products
SET available_quantity = CASE
                             WHEN code = 'P100' THEN 50
                             WHEN code = 'P101' THEN 30
                             WHEN code = 'P102' THEN 70
                             WHEN code = 'P103' THEN 20
                             WHEN code = 'P104' THEN 15
                             WHEN code = 'P105' THEN 60
                             WHEN code = 'P106' THEN 40
                             WHEN code = 'P107' THEN 25
                             WHEN code = 'P108' THEN 90
                             WHEN code = 'P109' THEN 100
                             WHEN code = 'P110' THEN 10
                             WHEN code = 'P111' THEN 80
                             WHEN code = 'P112' THEN 35
                             WHEN code = 'P113' THEN 55
                             WHEN code = 'P114' THEN 45
                             ELSE 10
    END;

-- Remove the default value constraint after updating the column
ALTER TABLE products ALTER COLUMN available_quantity DROP DEFAULT;

-- Create the product_inventory table
CREATE TABLE product_inventory
(
    inventory_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    order_id UUID NOT NULL,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    status TEXT NOT NULL,
    quantity INTEGER NOT NULL
);
