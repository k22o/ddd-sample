package com.example.dddsample.infrastructure.db.mapper;

import com.example.dddsample.infrastructure.db.record.CustomerRecord;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link CustomerMapper} の単体テスト。
 */
@MybatisTest
@SuppressWarnings({"NonAsciiCharacters"})
class CustomerMapperTest {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private void insertCustomer(final String id, final String email) {
        jdbcTemplate.update(
                "INSERT INTO customers (id, name, email, postal_code, prefecture, city, street, created_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                id, "山田太郎", email, "100-0001", "東京都", "千代田区", "1-1-1", LocalDateTime.now());
    }

    @Nested
    class FindById {

        @Test
        void 登録済みの顧客IDを指定すると該当レコードを返す() {
            insertCustomer("customer-1", "yamada@example.com");

            final CustomerRecord record = customerMapper.findById("customer-1");

            assertThat(record).isNotNull();
            assertThat(record.id()).isEqualTo("customer-1");
            assertThat(record.email()).isEqualTo("yamada@example.com");
        }

        @Test
        void 未登録の顧客IDを指定するとnullを返す() {
            final CustomerRecord record = customerMapper.findById("unknown");

            assertThat(record).isNull();
        }
    }
}
