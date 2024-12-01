-- Drop the tables if they already exist
DROP TABLE IF EXISTS order_history;
DROP TABLE IF EXISTS rating;

-- Create the order_history table
CREATE TABLE order_history (
                               id BIGSERIAL PRIMARY KEY,
                               order_id UUID NOT NULL,
                               product_id BIGINT NOT NULL,
                               customer_id BIGINT NOT NULL,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create the rating table
CREATE TABLE rating (
                        rating_id BIGSERIAL PRIMARY KEY,
                        customer_id BIGINT NOT NULL,
                        product_id BIGINT NOT NULL,
                        value DOUBLE PRECISION NOT NULL,
                        order_id UUID NOT NULL,
                        title TEXT,
                        content TEXT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
