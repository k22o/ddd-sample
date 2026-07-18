package com.example.dddsample.presentation.request;

import com.example.dddsample.application.dto.OrderItemDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.jspecify.annotations.NullMarked;

/**
 * 注文作成APIにおける注文明細のリクエストボディ。
 *
 * @param productId 商品ID
 * @param quantity  数量
 */
@NullMarked
public record OrderItemRequest(
        @NotBlank(message = "商品IDは必須です") String productId,
        @Positive(message = "数量は1以上である必要があります") int quantity) {

    /**
     * {@link OrderItemDto} に変換する。
     *
     * @return 変換後の {@link OrderItemDto}
     */
    public OrderItemDto toDto() {
        return new OrderItemDto(productId, quantity);
    }
}
