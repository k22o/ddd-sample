package com.example.dddsample.domain.model.order;

import com.example.dddsample.domain.model.product.ProductId;
import com.example.dddsample.domain.model.shared.Money;
import com.example.dddsample.domain.model.shared.Quantity;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link OrderItem} の単体テスト。
 */
@SuppressWarnings({"NonAsciiCharacters"})
class OrderItemTest {

    @Nested
    class Constructor {

        @Test
        void 商品名が空の場合はIllegalArgumentExceptionをスローする() {
            assertThatThrownBy(() -> new OrderItem(
                    new OrderItemId("item-1"),
                    new ProductId("product-1"),
                    " ",
                    new Money(new BigDecimal("1000"), "JPY"),
                    new Quantity(1)))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 商品名が入力されていれば生成できる() {
            final OrderItem item = new OrderItem(
                    new OrderItemId("item-1"),
                    new ProductId("product-1"),
                    "商品A",
                    new Money(new BigDecimal("1000"), "JPY"),
                    new Quantity(1));

            assertThat(item.productName()).isEqualTo("商品A");
        }
    }

    @Nested
    class Subtotal {

        @Test
        void 単価と数量を乗算した小計を返す() {
            final OrderItem item = new OrderItem(
                    new OrderItemId("item-1"),
                    new ProductId("product-1"),
                    "商品A",
                    new Money(new BigDecimal("1000"), "JPY"),
                    new Quantity(3));

            assertThat(item.subtotal()).isEqualTo(new Money(new BigDecimal("3000"), "JPY"));
        }
    }
}
