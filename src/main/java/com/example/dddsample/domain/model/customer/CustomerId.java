package com.example.dddsample.domain.model.customer;

import org.jspecify.annotations.NullMarked;

/**
 * 顧客IDを表す値オブジェクト。
 *
 * @param value UUID形式の顧客ID
 */
@NullMarked
public record CustomerId(String value) {

    /**
     * @throws IllegalArgumentException 値が空の場合
     */
    public CustomerId {
        if (value.isBlank()) {
            throw new IllegalArgumentException("顧客IDは必須です");
        }
    }
}
