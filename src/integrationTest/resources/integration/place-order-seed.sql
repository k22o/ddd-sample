INSERT INTO customers (id, name, email, postal_code, prefecture, city, street, created_at)
VALUES ('customer-1', '山田太郎', 'yamada@example.com', '100-0001', '東京都', '千代田区', '1-1-1', CURRENT_TIMESTAMP);

INSERT INTO products (id, name, price_amount, price_currency, description, created_at)
VALUES ('product-1', '商品A', 1000, 'JPY', '商品Aの説明', CURRENT_TIMESTAMP);
