package com.example.dddsample.domain.model.order;

import com.example.dddsample.domain.model.customer.CustomerId;
import com.example.dddsample.domain.model.product.ProductId;
import com.example.dddsample.domain.model.shared.Address;
import com.example.dddsample.domain.model.shared.Money;
import com.example.dddsample.domain.model.shared.Quantity;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link Order} の単体テスト。
 */
@SuppressWarnings({"NonAsciiCharacters"})
class OrderTest {

    private static final Address ADDRESS = new Address("100-0001", "東京都", "千代田区", "1-1-1");

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 7, 16, 10, 0);

    private OrderItem item(final String productId, final String price, final int quantity) {
        return new OrderItem(
                new OrderItemId("item-" + productId),
                new ProductId(productId),
                "商品" + productId,
                new Money(new BigDecimal(price), "JPY"),
                new Quantity(quantity));
    }

    @Nested
    class Constructor {

        @Test
        void 注文明細が空の場合はIllegalArgumentExceptionをスローする() {
            assertThatThrownBy(() -> new Order(
                    new OrderId("order-1"),
                    new CustomerId("customer-1"),
                    OrderStatus.PENDING,
                    List.of(),
                    new Money(BigDecimal.ZERO, "JPY"),
                    ADDRESS,
                    null,
                    CREATED_AT))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class Create {

        @Test
        void 注文明細から合計金額を自動計算しPENDING状態の注文を生成する() {
            final Order order = Order.create(
                    new OrderId("order-1"),
                    new CustomerId("customer-1"),
                    List.of(item("product-1", "1000", 2), item("product-2", "500", 1)),
                    ADDRESS,
                    CREATED_AT);

            assertThat(order.status()).isEqualTo(OrderStatus.PENDING);
            assertThat(order.totalAmount()).isEqualTo(new Money(new BigDecimal("2500"), "JPY"));
            assertThat(order.paymentId()).isNull();
        }

        @Test
        void 注文明細が空の場合はIllegalArgumentExceptionをスローする() {
            assertThatThrownBy(() -> Order.create(
                    new OrderId("order-1"), new CustomerId("customer-1"), List.of(), ADDRESS, CREATED_AT))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class Confirm {

        @Test
        void PENDING状態の注文は決済IDを伴いCONFIRMEDに遷移する() {
            final Order order = Order.create(
                    new OrderId("order-1"), new CustomerId("customer-1"),
                    List.of(item("product-1", "1000", 1)), ADDRESS, CREATED_AT);

            final Order confirmed = order.confirm("payment-1");

            assertThat(confirmed.status()).isEqualTo(OrderStatus.CONFIRMED);
            assertThat(confirmed.paymentId()).isEqualTo("payment-1");
        }

        @Test
        void CONFIRMED状態の注文を確定しようとするとIllegalStateExceptionをスローする() {
            final Order confirmed = Order.create(
                    new OrderId("order-1"), new CustomerId("customer-1"),
                    List.of(item("product-1", "1000", 1)), ADDRESS, CREATED_AT).confirm("payment-1");

            assertThatThrownBy(() -> confirmed.confirm("payment-2")).isInstanceOf(IllegalStateException.class);
        }

        @Test
        void CANCELLED状態の注文を確定しようとするとIllegalStateExceptionをスローする() {
            final Order cancelled = Order.create(
                    new OrderId("order-1"), new CustomerId("customer-1"),
                    List.of(item("product-1", "1000", 1)), ADDRESS, CREATED_AT).cancel();

            assertThatThrownBy(() -> cancelled.confirm("payment-1")).isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    class Cancel {

        @Test
        void PENDING状態の注文はCANCELLEDに遷移する() {
            final Order order = Order.create(
                    new OrderId("order-1"), new CustomerId("customer-1"),
                    List.of(item("product-1", "1000", 1)), ADDRESS, CREATED_AT);

            final Order cancelled = order.cancel();

            assertThat(cancelled.status()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        void CONFIRMED状態の注文をキャンセルしようとするとIllegalStateExceptionをスローする() {
            final Order confirmed = Order.create(
                    new OrderId("order-1"), new CustomerId("customer-1"),
                    List.of(item("product-1", "1000", 1)), ADDRESS, CREATED_AT).confirm("payment-1");

            assertThatThrownBy(confirmed::cancel).isInstanceOf(IllegalStateException.class);
        }

        @Test
        void CANCELLED状態の注文を再度キャンセルしようとするとIllegalStateExceptionをスローする() {
            final Order cancelled = Order.create(
                    new OrderId("order-1"), new CustomerId("customer-1"),
                    List.of(item("product-1", "1000", 1)), ADDRESS, CREATED_AT).cancel();

            assertThatThrownBy(cancelled::cancel).isInstanceOf(IllegalStateException.class);
        }
    }
}
