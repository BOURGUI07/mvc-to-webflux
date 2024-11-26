DROP TABLE IF EXISTS order_outbox;

-- Create the OrderOutbox table
CREATE TABLE order_outbox (
                              id BIGSERIAL PRIMARY KEY,
                              message BYTEA NOT NULL,
                              status TEXT NOT NULL
);