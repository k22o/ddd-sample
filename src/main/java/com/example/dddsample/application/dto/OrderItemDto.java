package com.example.dddsample.application.dto;

import org.jspecify.annotations.NullMarked;

/**
 * 注文作成時に指定する注文明細を表すDto。
 *
 * @param productId 商品ID
 * @param quantity  数量
 */
@NullMarked
public record OrderItemDto(String productId, int quantity) {
}
