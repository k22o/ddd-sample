package com.example.dddsample.domain.model.product;

import com.example.dddsample.domain.model.shared.Money;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * 商品を表す参照用集約。
 *
 * @param id          商品ID
 * @param name        商品名
 * @param price       価格
 * @param description 商品説明（未設定の場合は {@code null}）
 */
@NullMarked
public record Product(ProductId id, String name, Money price, @Nullable String description) {

    /**
     * @throws IllegalArgumentException 商品名が空の場合
     */
    public Product {
        if (name.isBlank()) {
            throw new IllegalArgumentException("商品名は必須です");
        }
    }
}
