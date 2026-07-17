package com.example.dddsample.presentation.request;

import com.example.dddsample.application.dto.PlaceOrderDto;
import com.example.dddsample.domain.model.shared.Address;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link PlaceOrderRequest} の単体テスト。
 */
@SuppressWarnings({"NonAsciiCharacters"})
class PlaceOrderRequestTest {

    @Nested
    class ToDto {

        @Test
        void PlaceOrderDtoへ変換できる() {
            final Address address = new Address("100-0001", "東京都", "千代田区", "1-1-1");
            final PlaceOrderRequest request = new PlaceOrderRequest(
                    "customer-1", address, List.of(new OrderItemRequest("product-1", 2)));

            final PlaceOrderDto dto = request.toDto();

            assertThat(dto.customerId()).isEqualTo("customer-1");
            assertThat(dto.shippingAddress()).isEqualTo(address);
            assertThat(dto.items()).hasSize(1);
            assertThat(dto.items().get(0).productId()).isEqualTo("product-1");
            assertThat(dto.items().get(0).quantity()).isEqualTo(2);
        }
    }
}
