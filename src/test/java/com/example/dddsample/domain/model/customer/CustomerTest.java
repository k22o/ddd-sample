package com.example.dddsample.domain.model.customer;

import com.example.dddsample.domain.model.shared.Address;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link Customer} の単体テスト。
 */
@SuppressWarnings({"NonAsciiCharacters"})
class CustomerTest {

    private static final Address ADDRESS = new Address("100-0001", "東京都", "千代田区", "1-1-1");

    @Nested
    class Constructor {

        @Test
        void 氏名が空の場合はIllegalArgumentExceptionをスローする() {
            assertThatThrownBy(() -> new Customer(new CustomerId("customer-1"), " ", "test@example.com", ADDRESS))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void メールアドレスが空の場合はIllegalArgumentExceptionをスローする() {
            assertThatThrownBy(() -> new Customer(new CustomerId("customer-1"), "山田太郎", " ", ADDRESS))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 氏名とメールアドレスが入力されていれば生成できる() {
            final Customer customer = new Customer(new CustomerId("customer-1"), "山田太郎", "test@example.com", ADDRESS);

            assertThat(customer.name()).isEqualTo("山田太郎");
        }
    }
}
