package com.example.dddsample.infrastructure.db.mapper;

import com.example.dddsample.infrastructure.db.record.OrderRecord;
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
 * {@link OrderMapper} の単体テスト。
 */
@MybatisTest
@SuppressWarnings({"NonAsciiCharacters"})
class OrderMapperTest {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUpCustomer() {
        jdbcTemplate.update(
                "INSERT INTO customers (id, name, email, postal_code, prefecture, city, street, created_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                "customer-1", "山田太郎", "yamada@example.com", "100-0001", "東京都", "千代田区", "1-1-1",
                LocalDateTime.now());
    }

    private void insertOrder(final String id, final String customerId, final String status) {
        jdbcTemplate.update(
                "INSERT INTO orders (id, customer_id, status, total_amount, total_currency, "
                        + "postal_code, prefecture, city, street, payment_id, created_at, updated_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                id, customerId, status, new BigDecimal("2000.00"), "JPY",
                "100-0001", "東京都", "千代田区", "1-1-1", null, LocalDateTime.now(), LocalDateTime.now());
    }

    @Nested
    class FindById {

        @Test
        void 登録済みの注文IDを指定すると該当レコードを返す() {
            insertOrder("order-1", "customer-1", "PENDING");

            final OrderRecord record = orderMapper.findById("order-1");

            assertThat(record).isNotNull();
            assertThat(record.id()).isEqualTo("order-1");
            assertThat(record.status()).isEqualTo("PENDING");
        }

        @Test
        void 未登録の注文IDを指定するとnullを返す() {
            final OrderRecord record = orderMapper.findById("unknown");

            assertThat(record).isNull();
        }
    }

    @Nested
    class FindByCustomerId {

        @Test
        void 顧客IDに紐づく注文レコード一覧を返す() {
            insertOrder("order-1", "customer-1", "PENDING");
            insertOrder("order-2", "customer-1", "CONFIRMED");

            final List<OrderRecord> records = orderMapper.findByCustomerId("customer-1");

            assertThat(records).extracting(OrderRecord::id).containsExactlyInAnyOrder("order-1", "order-2");
        }

        @Test
        void 紐づく注文が無い場合は空リストを返す() {
            final List<OrderRecord> records = orderMapper.findByCustomerId("customer-1");

            assertThat(records).isEmpty();
        }
    }

    @Nested
    class Insert {

        @Test
        void 注文レコードを新規登録できる() {
            final OrderRecord record = new OrderRecord(
                    "order-1", "customer-1", "PENDING", new BigDecimal("3000.00"), "JPY",
                    "100-0001", "東京都", "千代田区", "1-1-1", null, LocalDateTime.now());

            orderMapper.insert(record);

            final OrderRecord found = orderMapper.findById("order-1");
            assertThat(found).isNotNull();
            assertThat(found.totalAmount()).isEqualByComparingTo("3000.00");
        }
    }

    @Nested
    class UpdateStatusAndPayment {

        @Test
        void ステータスと支払いIDを更新できる() {
            insertOrder("order-1", "customer-1", "PENDING");

            orderMapper.updateStatusAndPayment("order-1", "CONFIRMED", "payment-1");

            final OrderRecord found = orderMapper.findById("order-1");
            assertThat(found).isNotNull();
            assertThat(found.status()).isEqualTo("CONFIRMED");
            assertThat(found.paymentId()).isEqualTo("payment-1");
        }
    }
}
