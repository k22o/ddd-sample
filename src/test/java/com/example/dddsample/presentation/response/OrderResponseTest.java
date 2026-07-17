package com.example.dddsample.presentation.response;

import com.example.dddsample.application.dto.OrderResultDto;
import com.example.dddsample.domain.model.order.OrderStatus;
import com.example.dddsample.domain.model.shared.Address;
import com.example.dddsample.domain.model.shared.Money;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link OrderResponse} の単体テスト。
 */
@SuppressWarnings({"NonAsciiCharacters"})
class OrderResponseTest {

    @Nested
    class From {

        @Test
        void OrderResultDtoからOrderResponseを組み立てられる() {
            final Address address = new Address("100-0001", "東京都", "千代田区", "1-1-1");
            final Money unitPrice = new Money(new BigDecimal("1000"), "JPY");
            final Money subtotal = new Money(new BigDecimal("2000"), "JPY");
            final LocalDateTime createdAt = LocalDateTime.of(2026, 7, 16, 10, 0);
            final OrderResultDto.Item item = new OrderResultDto.Item("product-1", "商品A", unitPrice, 2, subtotal);
            final OrderResultDto dto = new OrderResultDto(
                    "order-1", "customer-1", OrderStatus.PENDING, address, List.of(item), subtotal, null, createdAt);

            final OrderResponse response = OrderResponse.from(dto);

            assertThat(response.orderId()).isEqualTo("order-1");
            assertThat(response.customerId()).isEqualTo("customer-1");
            assertThat(response.status()).isEqualTo(OrderStatus.PENDING);
            assertThat(response.shippingAddress()).isEqualTo(address);
            assertThat(response.totalAmount()).isEqualTo(subtotal);
            assertThat(response.createdAt()).isEqualTo(createdAt);
            assertThat(response.items()).hasSize(1);
            assertThat(response.items().get(0)).isEqualTo(
                    new OrderResponse.Item("product-1", "商品A", unitPrice, 2, subtotal));
        }
    }
}
