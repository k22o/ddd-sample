package com.example.dddsample.domain.model.order;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link OrderId} の単体テスト。
 */
@SuppressWarnings({"NonAsciiCharacters"})
class OrderIdTest {

    @Nested
    class Constructor {

        @Test
        void 値が空の場合はIllegalArgumentExceptionをスローする() {
            assertThatThrownBy(() -> new OrderId(" ")).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 値が入力されていれば生成できる() {
            final OrderId orderId = new OrderId("order-1");

            assertThat(orderId.value()).isEqualTo("order-1");
        }
    }
}
