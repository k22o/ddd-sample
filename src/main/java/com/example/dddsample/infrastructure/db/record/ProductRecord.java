package com.example.dddsample.infrastructure.db.record;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;

/**
 * {@code products} テーブルのSELECT結果を受け取るPOJO。
 *
 * @param id            商品ID
 * @param name          商品名
 * @param priceAmount   価格
 * @param priceCurrency 価格の通貨コード
 * @param description   商品説明
 */
@NullMarked
public record ProductRecord(
        String id,                         // 商品ID
        String name,                       // 商品名
        BigDecimal priceAmount,            // 価格
        String priceCurrency,              // 価格の通貨コード
        @Nullable String description) {    // 商品説明（未設定の場合は null）
}
