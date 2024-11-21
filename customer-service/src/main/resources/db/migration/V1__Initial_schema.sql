-- Enable the UUID extension (required for UUID generation in PostgreSQL)
CREATE SEQUENCE customer_id_seq START WITH 1 INCREMENT BY 50;

-- Create the customer table
CREATE TABLE customer (
                          id BIGINT PRIMARY KEY DEFAULT nextval('customer_id_seq'), -- Explicitly define PRIMARY KEY
                          username VARCHAR(255) NOT NULL UNIQUE,
                          balance NUMERIC NOT NULL,
                          email VARCHAR(255) NOT NULL UNIQUE,
                          phone VARCHAR(15),
                          street VARCHAR(255),
                          city VARCHAR(255),
                          country VARCHAR(255),
                          state VARCHAR(255)
);

-- Create the customerpayment table
CREATE TABLE customer_payment (
                                 payment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- Auto-generate UUID
                                 order_id UUID NOT NULL,      -- Auto-generate UUID
                                 customer_id BIGINT NOT NULL,
                                 status TEXT NOT NULL,
                                 amount NUMERIC NOT NULL,
                                 FOREIGN KEY (customer_id) REFERENCES customer (id) ON DELETE CASCADE
);


-- Sample data for the customer table
INSERT INTO customer (username, balance, email, phone, street, city, country, state)
VALUES
    ('john_doe', 2500, 'john.doe@example.com', '1234567890', '123 Main St', 'Springfield', 'USA', 'IL'),
    ('jane_smith', 3200, 'jane.smith@example.com', '0987654321', '456 Elm St', 'Shelbyville', 'USA', 'TX'),
    ('alice_johnson', 4000, 'alice.johnson@example.com', '5551234567', '789 Oak St', 'Metropolis', 'USA', 'NY'),
    ('bob_brown', 4700, 'bob.brown@example.com', '1112223333', '101 Pine St', 'Gotham', 'USA', 'CA'),
    ('eve_white', 2800, 'eve.white@example.com', '4445556666', '202 Cedar St', 'Star City', 'USA', 'FL');
