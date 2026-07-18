package com.example.dddsample.presentation.request;

import com.example.dddsample.application.dto.PlaceOrderDto;
import com.example.dddsample.domain.model.shared.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * 注文作成APIのリクエストボディ。
 *
 * @param customerId      顧客ID
 * @param shippingAddress 配送先住所
 * @param items           注文明細
 */
@NullMarked
public record PlaceOrderRequest(
        @NotBlank(message = "顧客IDは必須です") String customerId,
        @NotNull(message = "配送先住所は必須です") Address shippingAddress,
        @NotEmpty(message = "注文明細は1件以上指定してください") @Valid List<OrderItemRequest> items) {

    /**
     * {@link PlaceOrderDto} に変換する。
     *
     * @return 変換後の {@link PlaceOrderDto}
     */
    public PlaceOrderDto toDto() {
        return new PlaceOrderDto(customerId, shippingAddress, items.stream().map(OrderItemRequest::toDto).toList());
    }
}
