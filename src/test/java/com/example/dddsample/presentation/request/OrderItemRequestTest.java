package com.example.dddsample.presentation.request;

import com.example.dddsample.application.dto.OrderItemDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link OrderItemRequest} の単体テスト。
 */
@SuppressWarnings({"NonAsciiCharacters"})
class OrderItemRequestTest {

    @Nested
    class ToDto {

        @Test
        void OrderItemDtoへ変換できる() {
            final OrderItemRequest request = new OrderItemRequest("product-1", 3);

            final OrderItemDto dto = request.toDto();

            assertThat(dto.productId()).isEqualTo("product-1");
            assertThat(dto.quantity()).isEqualTo(3);
        }
    }
}
