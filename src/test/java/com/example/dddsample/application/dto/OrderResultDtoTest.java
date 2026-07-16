package com.example.dddsample.application.dto;

import com.example.dddsample.domain.model.customer.CustomerId;
import com.example.dddsample.domain.model.order.Order;
import com.example.dddsample.domain.model.order.OrderId;
import com.example.dddsample.domain.model.order.OrderItem;
import com.example.dddsample.domain.model.order.OrderItemId;
import com.example.dddsample.domain.model.order.OrderStatus;
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

/**
 * {@link OrderResultDto} の単体テスト。
 */
@SuppressWarnings({"NonAsciiCharacters"})
class OrderResultDtoTest {

    private static final Address ADDRESS = new Address("100-0001", "東京都", "千代田区", "1-1-1");

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 7, 16, 10, 0);

    @Nested
    class From {

        @Test
        void Order集約からDtoへ全項目を変換する() {
            final OrderItem orderItem = new OrderItem(
                    new OrderItemId("item-1"),
                    new ProductId("product-1"),
                    "商品A",
                    new Money(new BigDecimal("1000"), "JPY"),
                    new Quantity(2));
            final Order order = Order.create(
                    new OrderId("order-1"), new CustomerId("customer-1"), List.of(orderItem), ADDRESS, CREATED_AT)
                    .confirm("payment-1");

            final OrderResultDto dto = OrderResultDto.from(order);

            assertThat(dto.orderId()).isEqualTo("order-1");
            assertThat(dto.customerId()).isEqualTo("customer-1");
            assertThat(dto.status()).isEqualTo(OrderStatus.CONFIRMED);
            assertThat(dto.shippingAddress()).isEqualTo(ADDRESS);
            assertThat(dto.totalAmount()).isEqualTo(new Money(new BigDecimal("2000"), "JPY"));
            assertThat(dto.paymentId()).isEqualTo("payment-1");
            assertThat(dto.createdAt()).isEqualTo(CREATED_AT);

            assertThat(dto.items()).hasSize(1);
            final OrderResultDto.Item item = dto.items().get(0);
            assertThat(item.productId()).isEqualTo("product-1");
            assertThat(item.productName()).isEqualTo("商品A");
            assertThat(item.unitPrice()).isEqualTo(new Money(new BigDecimal("1000"), "JPY"));
            assertThat(item.quantity()).isEqualTo(2);
            assertThat(item.subtotal()).isEqualTo(new Money(new BigDecimal("2000"), "JPY"));
        }

        @Test
        void 未確定の注文は決済IDがnullとして変換される() {
            final OrderItem orderItem = new OrderItem(
                    new OrderItemId("item-1"),
                    new ProductId("product-1"),
                    "商品A",
                    new Money(new BigDecimal("1000"), "JPY"),
                    new Quantity(1));
            final Order order = Order.create(
                    new OrderId("order-1"), new CustomerId("customer-1"), List.of(orderItem), ADDRESS, CREATED_AT);

            final OrderResultDto dto = OrderResultDto.from(order);

            assertThat(dto.paymentId()).isNull();
        }
    }
}
