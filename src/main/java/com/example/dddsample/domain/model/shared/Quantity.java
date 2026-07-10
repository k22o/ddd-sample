package com.example.dddsample.domain.model.shared;

import org.jspecify.annotations.NullMarked;

/**
 * 数量を表す値オブジェクト。1以上の正整数のみ有効。
 *
 * @param value 数量
 */
@NullMarked
public record Quantity(int value) {

    /**
     * @throws IllegalArgumentException 数量が1未満の場合
     */
    public Quantity {
        if (value < 1) {
            throw new IllegalArgumentException("数量は1以上である必要があります");
        }
    }
}
