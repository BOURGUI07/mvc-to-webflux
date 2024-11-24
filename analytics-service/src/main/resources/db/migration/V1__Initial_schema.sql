CREATE TABLE product_view (
                              id BIGSERIAL PRIMARY KEY,
                              product_code VARCHAR(255) NOT NULL UNIQUE,
                              view_count BIGINT
);