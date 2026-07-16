package com.example.dddsample.domain.model.shared;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link Quantity} の単体テスト。
 */
@SuppressWarnings({"NonAsciiCharacters"})
class QuantityTest {

    @Nested
    class Constructor {

        @Test
        void 数量が0の場合はIllegalArgumentExceptionをスローする() {
            assertThatThrownBy(() -> new Quantity(0)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 数量が負値の場合はIllegalArgumentExceptionをスローする() {
            assertThatThrownBy(() -> new Quantity(-1)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 数量が1以上であれば生成できる() {
            final Quantity quantity = new Quantity(1);

            assertThat(quantity.value()).isEqualTo(1);
        }
    }
}
