package com.example.dddsample.application.dto;

import com.example.dddsample.domain.model.shared.Address;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * 注文作成（UC-1）の入力を表すDto。
 *
 * @param customerId      顧客ID
 * @param shippingAddress 配送先住所
 * @param items           注文明細
 */
@NullMarked
public record PlaceOrderDto(String customerId, Address shippingAddress, List<OrderItemDto> items) {
}
