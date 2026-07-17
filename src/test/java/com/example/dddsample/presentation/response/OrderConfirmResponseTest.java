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
 * {@link OrderConfirmResponse} の単体テスト。
 */
@SuppressWarnings({"NonAsciiCharacters"})
class OrderConfirmResponseTest {

    @Nested
    class From {

        @Test
        void OrderResultDtoからOrderConfirmResponseを組み立てられる() {
            final Address address = new Address("100-0001", "東京都", "千代田区", "1-1-1");
            final Money totalAmount = new Money(new BigDecimal("2000"), "JPY");
            final OrderResultDto dto = new OrderResultDto(
                    "order-1",
                    "customer-1",
                    OrderStatus.CONFIRMED,
                    address,
                    List.of(),
                    totalAmount,
                    "payment-1",
                    LocalDateTime.now());

            final OrderConfirmResponse response = OrderConfirmResponse.from(dto);

            assertThat(response.orderId()).isEqualTo("order-1");
            assertThat(response.status()).isEqualTo(OrderStatus.CONFIRMED);
            assertThat(response.paymentId()).isEqualTo("payment-1");
        }
    }
}
