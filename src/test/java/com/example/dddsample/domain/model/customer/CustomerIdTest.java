package com.example.dddsample.domain.model.customer;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link CustomerId} の単体テスト。
 */
@SuppressWarnings({"NonAsciiCharacters"})
class CustomerIdTest {

    @Nested
    class Constructor {

        @Test
        void 値が空の場合はIllegalArgumentExceptionをスローする() {
            assertThatThrownBy(() -> new CustomerId(" ")).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 値が入力されていれば生成できる() {
            final CustomerId customerId = new CustomerId("customer-1");

            assertThat(customerId.value()).isEqualTo("customer-1");
        }
    }
}
