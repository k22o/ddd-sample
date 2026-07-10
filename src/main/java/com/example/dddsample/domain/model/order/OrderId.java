package com.example.dddsample.domain.model.order;

import org.jspecify.annotations.NullMarked;

/**
 * 注文IDを表す値オブジェクト。
 *
 * @param value UUID形式の注文ID
 */
@NullMarked
public record OrderId(String value) {

    /**
     * @throws IllegalArgumentException 値が空の場合
     */
    public OrderId {
        if (value.isBlank()) {
            throw new IllegalArgumentException("注文IDは必須です");
        }
    }
}
