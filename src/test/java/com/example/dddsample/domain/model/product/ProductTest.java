package com.example.dddsample.domain.model.product;

import com.example.dddsample.domain.model.shared.Money;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link Product} の単体テスト。
 */
@SuppressWarnings({"NonAsciiCharacters"})
class ProductTest {

    @Nested
    class Constructor {

        @Test
        void 商品名が空の場合はIllegalArgumentExceptionをスローする() {
            assertThatThrownBy(() -> new Product(
                    new ProductId("product-1"), " ", new Money(new BigDecimal("1000"), "JPY"), null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 商品名が入力されていれば生成できる() {
            final Product product = new Product(
                    new ProductId("product-1"), "商品A", new Money(new BigDecimal("1000"), "JPY"), "説明");

            assertThat(product.name()).isEqualTo("商品A");
        }

        @Test
        void 説明がnullでも生成できる() {
            final Product product = new Product(
                    new ProductId("product-1"), "商品A", new Money(new BigDecimal("1000"), "JPY"), null);

            assertThat(product.description()).isNull();
        }
    }
}
