package com.example.dddsample.domain.model.order;

import org.jspecify.annotations.NullMarked;

/**
 * 注文明細IDを表す値オブジェクト。
 *
 * @param value UUID形式の注文明細ID
 */
@NullMarked
public record OrderItemId(String value) {

    /**
     * @throws IllegalArgumentException 値が空の場合
     */
    public OrderItemId {
        if (value.isBlank()) {
            throw new IllegalArgumentException("注文明細IDは必須です");
        }
    }
}
