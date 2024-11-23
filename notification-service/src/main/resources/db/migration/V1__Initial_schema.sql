-- Create customer table with auto-incrementing primary key
CREATE TABLE customer (
                          id BIGSERIAL PRIMARY KEY,
                          customer_id BIGINT UNIQUE NOT NULL,
                          username VARCHAR(255) NOT NULL,
                          email VARCHAR(255) NOT NULL
);

-- Create orders table with auto-incrementing primary key
CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        order_id UUID NOT NULL UNIQUE

);
