package com.example.dddsample.presentation.request;

import com.example.dddsample.application.dto.OrderItemDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link OrderItemRequest} の単体テスト。
 */
@SuppressWarnings({"NonAsciiCharacters"})
class OrderItemRequestTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @Nested
    class Constraints {

        @Test
        void 全項目が正しい場合は違反がない() {
            final OrderItemRequest request = new OrderItemRequest("product-1", 2);

            final Set<ConstraintViolation<OrderItemRequest>> violations = VALIDATOR.validate(request);

            assertThat(violations).isEmpty();
        }

        @Test
        void 商品IDがnullの場合は違反になる() {
            final OrderItemRequest request = new OrderItemRequest(null, 1);

            final Set<ConstraintViolation<OrderItemRequest>> violations = VALIDATOR.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("商品IDは必須です");
        }

        @Test
        void 商品IDが空文字の場合は違反になる() {
            final OrderItemRequest request = new OrderItemRequest(" ", 1);

            final Set<ConstraintViolation<OrderItemRequest>> violations = VALIDATOR.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("商品IDは必須です");
        }

        @Test
        void 数量が1未満の場合は違反になる() {
            final OrderItemRequest request = new OrderItemRequest("product-1", 0);

            final Set<ConstraintViolation<OrderItemRequest>> violations = VALIDATOR.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("数量は1以上である必要があります");
        }
    }

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
