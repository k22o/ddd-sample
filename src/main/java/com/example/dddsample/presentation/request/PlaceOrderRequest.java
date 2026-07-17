package com.example.dddsample.presentation.request;

import com.example.dddsample.application.dto.PlaceOrderDto;
import com.example.dddsample.domain.model.shared.Address;
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
public record PlaceOrderRequest(String customerId, Address shippingAddress, List<OrderItemRequest> items) {

    /**
     * {@link PlaceOrderDto} に変換する。
     *
     * @return 変換後の {@link PlaceOrderDto}
     */
    public PlaceOrderDto toDto() {
        return new PlaceOrderDto(customerId, shippingAddress, items.stream().map(OrderItemRequest::toDto).toList());
    }
}
