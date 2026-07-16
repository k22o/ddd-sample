package com.example.dddsample.infrastructure.db.mapper;

import com.example.dddsample.infrastructure.db.record.OrderItemRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link OrderItemMapper} の単体テスト。
 */
@MybatisTest
@SuppressWarnings({"NonAsciiCharacters"})
class OrderItemMapperTest {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUpCustomerAndOrders() {
        jdbcTemplate.update(
                "INSERT INTO customers (id, name, email, postal_code, prefecture, city, street, created_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                "customer-1", "山田太郎", "yamada@example.com", "100-0001", "東京都", "千代田区", "1-1-1",
                LocalDateTime.now());
        insertOrder("order-1");
        insertOrder("order-2");
    }

    private void insertOrder(final String id) {
        jdbcTemplate.update(
                "INSERT INTO orders (id, customer_id, status, total_amount, total_currency, "
                        + "postal_code, prefecture, city, street, payment_id, created_at, updated_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                id, "customer-1", "PENDING", new BigDecimal("2000.00"), "JPY",
                "100-0001", "東京都", "千代田区", "1-1-1", null, LocalDateTime.now(), LocalDateTime.now());
    }

    private void insertOrderItem(final String id, final String orderId) {
        jdbcTemplate.update(
                "INSERT INTO order_items (id, order_id, product_id, product_name, "
                        + "unit_price_amount, unit_price_currency, quantity) VALUES (?, ?, ?, ?, ?, ?, ?)",
                id, orderId, "product-1", "商品A", new BigDecimal("1000.00"), "JPY", 2);
    }

    @Nested
    class FindByOrderId {

        @Test
        void 注文IDに紐づく明細レコード一覧を返す() {
            insertOrderItem("item-1", "order-1");
            insertOrderItem("item-2", "order-1");
            insertOrderItem("item-3", "order-2");

            final List<OrderItemRecord> records = orderItemMapper.findByOrderId("order-1");

            assertThat(records).extracting(OrderItemRecord::id).containsExactlyInAnyOrder("item-1", "item-2");
        }
    }

    @Nested
    class FindByOrderIds {

        @Test
        void 複数の注文IDに紐づく明細レコード一覧を一括取得できる() {
            insertOrderItem("item-1", "order-1");
            insertOrderItem("item-2", "order-1");
            insertOrderItem("item-3", "order-2");

            final List<OrderItemRecord> records = orderItemMapper.findByOrderIds(List.of("order-1", "order-2"));

            assertThat(records).extracting(OrderItemRecord::id)
                    .containsExactlyInAnyOrder("item-1", "item-2", "item-3");
        }
    }

    @Nested
    class InsertAll {

        @Test
        void 複数の明細レコードを一括登録できる() {
            final List<OrderItemRecord> records = List.of(
                    new OrderItemRecord("item-1", "order-1", "product-1", "商品A",
                            new BigDecimal("1000.00"), "JPY", 1),
                    new OrderItemRecord("item-2", "order-1", "product-2", "商品B",
                            new BigDecimal("1500.00"), "JPY", 3));

            orderItemMapper.insertAll(records);

            final List<OrderItemRecord> found = orderItemMapper.findByOrderId("order-1");
            assertThat(found).hasSize(2);
        }
    }
}
