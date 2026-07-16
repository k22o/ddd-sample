package com.example.dddsample.infrastructure.db.mapper;

import com.example.dddsample.infrastructure.db.record.ProductRecord;
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
 * {@link ProductMapper} の単体テスト。
 */
@MybatisTest
@SuppressWarnings({"NonAsciiCharacters"})
class ProductMapperTest {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private void insertProduct(final String id, final String name) {
        jdbcTemplate.update(
                "INSERT INTO products (id, name, price_amount, price_currency, description, created_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?)",
                id, name, new BigDecimal("1000.00"), "JPY", "説明", LocalDateTime.now());
    }

    @Nested
    class FindById {

        @Test
        void 登録済みの商品IDを指定すると該当レコードを返す() {
            insertProduct("product-1", "商品A");

            final ProductRecord record = productMapper.findById("product-1");

            assertThat(record).isNotNull();
            assertThat(record.id()).isEqualTo("product-1");
            assertThat(record.name()).isEqualTo("商品A");
        }

        @Test
        void 未登録の商品IDを指定するとnullを返す() {
            final ProductRecord record = productMapper.findById("unknown");

            assertThat(record).isNull();
        }
    }

    @Nested
    class FindByIds {

        @Test
        void 複数の商品IDを指定すると該当レコード一覧を返す() {
            insertProduct("product-1", "商品A");
            insertProduct("product-2", "商品B");
            insertProduct("product-3", "商品C");

            final List<ProductRecord> records = productMapper.findByIds(List.of("product-1", "product-3"));

            assertThat(records).extracting(ProductRecord::id).containsExactlyInAnyOrder("product-1", "product-3");
        }

        @Test
        void 該当する商品が無い場合は空リストを返す() {
            final List<ProductRecord> records = productMapper.findByIds(List.of("unknown"));

            assertThat(records).isEmpty();
        }
    }
}
