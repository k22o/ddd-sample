package com.example.dddsample.infrastructure.db.record;

import org.jspecify.annotations.NullMarked;

import java.math.BigDecimal;

/**
 * {@code order_items} テーブルのSELECT結果を受け取るPOJO。
 *
 * @param id                 注文明細ID
 * @param orderId            注文ID
 * @param productId          商品ID
 * @param productName        注文時点の商品名（スナップショット）
 * @param unitPriceAmount    注文時点の単価（スナップショット）
 * @param unitPriceCurrency  単価の通貨コード
 * @param quantity           数量
 */
@NullMarked
public record OrderItemRecord(
        String id,                     // 注文明細ID
        String orderId,                // 注文ID
        String productId,              // 商品ID
        String productName,            // 注文時点の商品名（スナップショット）
        BigDecimal unitPriceAmount,    // 注文時点の単価（スナップショット）
        String unitPriceCurrency,      // 単価の通貨コード
        int quantity) {                // 数量
}
