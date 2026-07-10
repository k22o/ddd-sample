package com.example.dddsample.domain.model.order;

import com.example.dddsample.domain.model.product.ProductId;
import com.example.dddsample.domain.model.shared.Money;
import com.example.dddsample.domain.model.shared.Quantity;
import org.jspecify.annotations.NullMarked;

/**
 * 注文明細を表す、{@link Order} 集約内のEntity。
 *
 * @param id          注文明細ID
 * @param productId   商品ID
 * @param productName 注文時点の商品名（スナップショット）
 * @param unitPrice   注文時点の単価（スナップショット）
 * @param quantity    数量
 */
@NullMarked
public record OrderItem(OrderItemId id, ProductId productId, String productName, Money unitPrice, Quantity quantity) {

    /**
     * @throws IllegalArgumentException 商品名が空の場合
     */
    public OrderItem {
        if (productName.isBlank()) {
            throw new IllegalArgumentException("商品名は必須です");
        }
    }

    /**
     * 小計金額（単価 × 数量）を算出する。
     *
     * @return 小計金額
     */
    public Money subtotal() {
        return unitPrice.multiply(quantity.value());
    }
}
