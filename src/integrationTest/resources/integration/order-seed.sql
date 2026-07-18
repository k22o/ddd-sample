INSERT INTO customers (id, name, email, postal_code, prefecture, city, street, created_at)
VALUES ('customer-1', '山田太郎', 'yamada@example.com', '100-0001', '東京都', '千代田区', '1-1-1', CURRENT_TIMESTAMP);

INSERT INTO products (id, name, price_amount, price_currency, description, created_at)
VALUES ('product-1', '商品A', 1000, 'JPY', '商品Aの説明', CURRENT_TIMESTAMP);

INSERT INTO orders (id, customer_id, status, total_amount, total_currency, postal_code, prefecture, city, street, payment_id, created_at, updated_at)
VALUES ('order-1', 'customer-1', 'PENDING', 2000, 'JPY', '100-0001', '東京都', '千代田区', '1-1-1', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO order_items (id, order_id, product_id, product_name, unit_price_amount, unit_price_currency, quantity)
VALUES ('order-item-1', 'order-1', 'product-1', '商品A', 1000, 'JPY', 2);
