package com.example.dddsample.domain.model.product;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link ProductId} の単体テスト。
 */
@SuppressWarnings({"NonAsciiCharacters"})
class ProductIdTest {

    @Nested
    class Constructor {

        @Test
        void 値が空の場合はIllegalArgumentExceptionをスローする() {
            assertThatThrownBy(() -> new ProductId(" ")).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 値が入力されていれば生成できる() {
            final ProductId productId = new ProductId("product-1");

            assertThat(productId.value()).isEqualTo("product-1");
        }
    }
}
