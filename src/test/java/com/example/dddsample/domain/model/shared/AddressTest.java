package com.example.dddsample.domain.model.shared;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link Address} の単体テスト。
 */
@SuppressWarnings({"NonAsciiCharacters"})
class AddressTest {

    @Nested
    class Constructor {

        @Test
        void 郵便番号が空の場合はIllegalArgumentExceptionをスローする() {
            assertThatThrownBy(() -> new Address(" ", "東京都", "千代田区", "1-1-1"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 都道府県が空の場合はIllegalArgumentExceptionをスローする() {
            assertThatThrownBy(() -> new Address("100-0001", " ", "千代田区", "1-1-1"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 市区町村が空の場合はIllegalArgumentExceptionをスローする() {
            assertThatThrownBy(() -> new Address("100-0001", "東京都", " ", "1-1-1"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 番地建物名が空の場合はIllegalArgumentExceptionをスローする() {
            assertThatThrownBy(() -> new Address("100-0001", "東京都", "千代田区", " "))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 全項目が入力されていれば生成できる() {
            final Address address = new Address("100-0001", "東京都", "千代田区", "1-1-1");

            assertThat(address.postalCode()).isEqualTo("100-0001");
        }
    }
}
