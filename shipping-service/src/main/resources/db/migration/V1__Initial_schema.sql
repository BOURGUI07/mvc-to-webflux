CREATE TABLE shipment (
                                  shipping_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- Auto-generate UUID
                                  order_id UUID NOT NULL,      -- Auto-generate UUID
                                  customer_id BIGINT NOT NULL,
                                  product_id BIGINT NOT NULL,
                                  status TEXT NOT NULL,
                                  quantity INT NOT NULL,
                                    deliveryDate TIMESTAMP
);