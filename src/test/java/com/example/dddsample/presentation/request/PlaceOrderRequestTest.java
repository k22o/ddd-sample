package com.example.dddsample.presentation.request;

import com.example.dddsample.application.dto.PlaceOrderDto;
import com.example.dddsample.domain.model.shared.Address;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link PlaceOrderRequest} の単体テスト。
 */
@SuppressWarnings({"NonAsciiCharacters"})
class PlaceOrderRequestTest {

    private static final Address ADDRESS = new Address("100-0001", "東京都", "千代田区", "1-1-1");

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @Nested
    class Constraints {

        @Test
        void 全項目が正しい場合は違反がない() {
            final PlaceOrderRequest request = new PlaceOrderRequest(
                    "customer-1", ADDRESS, List.of(new OrderItemRequest("product-1", 2)));

            final Set<ConstraintViolation<PlaceOrderRequest>> violations = VALIDATOR.validate(request);

            assertThat(violations).isEmpty();
        }

        @Test
        void 顧客IDがnullの場合は違反になる() {
            final PlaceOrderRequest request = new PlaceOrderRequest(
                    null, ADDRESS, List.of(new OrderItemRequest("product-1", 2)));

            final Set<ConstraintViolation<PlaceOrderRequest>> violations = VALIDATOR.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("顧客IDは必須です");
        }

        @Test
        void 顧客IDが空文字の場合は違反になる() {
            final PlaceOrderRequest request = new PlaceOrderRequest(
                    " ", ADDRESS, List.of(new OrderItemRequest("product-1", 2)));

            final Set<ConstraintViolation<PlaceOrderRequest>> violations = VALIDATOR.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("顧客IDは必須です");
        }

        @Test
        void 配送先住所がnullの場合は違反になる() {
            final PlaceOrderRequest request = new PlaceOrderRequest(
                    "customer-1", null, List.of(new OrderItemRequest("product-1", 2)));

            final Set<ConstraintViolation<PlaceOrderRequest>> violations = VALIDATOR.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("配送先住所は必須です");
        }

        @Test
        void 注文明細が空の場合は違反になる() {
            final PlaceOrderRequest request = new PlaceOrderRequest("customer-1", ADDRESS, List.of());

            final Set<ConstraintViolation<PlaceOrderRequest>> violations = VALIDATOR.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("注文明細は1件以上指定してください");
        }

        @Test
        void 注文明細の項目が不正な場合は違反になる() {
            final PlaceOrderRequest request = new PlaceOrderRequest(
                    "customer-1", ADDRESS, List.of(new OrderItemRequest("product-1", 0)));

            final Set<ConstraintViolation<PlaceOrderRequest>> violations = VALIDATOR.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("数量は1以上である必要があります");
        }
    }

    @Nested
    class ToDto {

        @Test
        void PlaceOrderDtoへ変換できる() {
            final PlaceOrderRequest request = new PlaceOrderRequest(
                    "customer-1", ADDRESS, List.of(new OrderItemRequest("product-1", 2)));

            final PlaceOrderDto dto = request.toDto();

            assertThat(dto.customerId()).isEqualTo("customer-1");
            assertThat(dto.shippingAddress()).isEqualTo(ADDRESS);
            assertThat(dto.items()).hasSize(1);
            assertThat(dto.items().get(0).productId()).isEqualTo("product-1");
            assertThat(dto.items().get(0).quantity()).isEqualTo(2);
        }
    }
}
