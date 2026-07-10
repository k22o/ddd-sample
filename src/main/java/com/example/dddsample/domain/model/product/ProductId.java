package com.example.dddsample.domain.model.product;

import org.jspecify.annotations.NullMarked;

/**
 * 商品IDを表す値オブジェクト。
 *
 * @param value UUID形式の商品ID
 */
@NullMarked
public record ProductId(String value) {

    /**
     * @throws IllegalArgumentException 値が空の場合
     */
    public ProductId {
        if (value.isBlank()) {
            throw new IllegalArgumentException("商品IDは必須です");
        }
    }
}
