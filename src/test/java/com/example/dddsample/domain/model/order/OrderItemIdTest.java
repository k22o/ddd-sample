package com.example.dddsample.domain.model.order;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link OrderItemId} の単体テスト。
 */
@SuppressWarnings({"NonAsciiCharacters"})
class OrderItemIdTest {

    @Nested
    class Constructor {

        @Test
        void 値が空の場合はIllegalArgumentExceptionをスローする() {
            assertThatThrownBy(() -> new OrderItemId(" ")).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 値が入力されていれば生成できる() {
            final OrderItemId orderItemId = new OrderItemId("item-1");

            assertThat(orderItemId.value()).isEqualTo("item-1");
        }
    }
}
