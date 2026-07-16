package com.example.dddsample.domain.model.shared;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link Money} の単体テスト。
 */
@SuppressWarnings({"NonAsciiCharacters"})
class MoneyTest {

    @Nested
    class Constructor {

        @Test
        void 金額が負値の場合はIllegalArgumentExceptionをスローする() {
            assertThatThrownBy(() -> new Money(new BigDecimal("-1"), "JPY"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 通貨コードが空の場合はIllegalArgumentExceptionをスローする() {
            assertThatThrownBy(() -> new Money(BigDecimal.TEN, " "))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 金額が0以上であれば生成できる() {
            final Money money = new Money(BigDecimal.ZERO, "JPY");

            assertThat(money.amount()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Nested
    class Of {

        @Test
        void 金額と通貨コードを指定してインスタンスを生成する() {
            final Money money = Money.of(new BigDecimal("100"), "JPY");

            assertThat(money).isEqualTo(new Money(new BigDecimal("100"), "JPY"));
        }
    }

    @Nested
    class Add {

        @Test
        void 同一通貨の金額同士は加算できる() {
            final Money a = new Money(new BigDecimal("100"), "JPY");
            final Money b = new Money(new BigDecimal("200"), "JPY");

            assertThat(a.add(b)).isEqualTo(new Money(new BigDecimal("300"), "JPY"));
        }

        @Test
        void 異なる通貨同士を加算するとIllegalArgumentExceptionをスローする() {
            final Money a = new Money(new BigDecimal("100"), "JPY");
            final Money b = new Money(new BigDecimal("100"), "USD");

            assertThatThrownBy(() -> a.add(b)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class Multiply {

        @Test
        void 数量を乗算した金額を返す() {
            final Money unitPrice = new Money(new BigDecimal("1000"), "JPY");

            assertThat(unitPrice.multiply(3)).isEqualTo(new Money(new BigDecimal("3000"), "JPY"));
        }
    }
}
