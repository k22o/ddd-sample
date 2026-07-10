package com.example.dddsample.domain.client;

import com.example.dddsample.domain.model.product.ProductId;
import com.example.dddsample.domain.model.shared.Quantity;
import org.jspecify.annotations.NullMarked;

/**
 * 外部在庫管理サービスとのインターフェース。
 */
@NullMarked
public interface InventoryClient {

    /**
     * 指定商品の在庫が確保可能かを確認する。
     *
     * @param productId 商品ID
     * @param quantity  確認する数量
     * @return 在庫が確保可能な場合 {@code true}
     */
    boolean checkStock(ProductId productId, Quantity quantity);

    /**
     * 在庫を引き当てる。
     *
     * @param productId 商品ID
     * @param quantity  引当数量
     * @return 引当ID
     */
    String reserve(ProductId productId, Quantity quantity);

    /**
     * 在庫引当をキャンセルする。
     *
     * @param reservationId キャンセル対象の引当ID
     */
    void cancelReservation(String reservationId);
}
