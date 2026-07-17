package com.example.dddsample.presentation.response;

import com.example.dddsample.application.dto.OrderResultDto;
import com.example.dddsample.domain.model.order.OrderStatus;
import com.example.dddsample.domain.model.shared.Address;
import com.example.dddsample.domain.model.shared.Money;
import org.jspecify.annotations.NullMarked;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 注文情報のレスポンスボディ。注文作成・注文取得APIで共通利用する。
 *
 * @param orderId         注文ID
 * @param customerId      顧客ID
 * @param status          注文ステータス
 * @param shippingAddress 配送先住所
 * @param items           注文明細
 * @param totalAmount     合計金額
 * @param createdAt       注文日時
 */
@NullMarked
public record OrderResponse(
        String orderId,
        String customerId,
        OrderStatus status,
        Address shippingAddress,
        List<Item> items,
        Money totalAmount,
        LocalDateTime createdAt) {

    /**
     * {@link OrderResultDto} から {@link OrderResponse} を組み立てる。
     *
     * @param dto 変換元の注文処理結果
     * @return 組み立てられた {@link OrderResponse}
     */
    public static OrderResponse from(final OrderResultDto dto) {
        return new OrderResponse(
                dto.orderId(),
                dto.customerId(),
                dto.status(),
                dto.shippingAddress(),
                dto.items().stream().map(Item::from).toList(),
                dto.totalAmount(),
                dto.createdAt());
    }

    /**
     * 注文明細のレスポンス。
     *
     * @param productId   商品ID
     * @param productName 注文時点の商品名
     * @param unitPrice   注文時点の単価
     * @param quantity    数量
     * @param subtotal    小計金額
     */
    public record Item(String productId, String productName, Money unitPrice, int quantity, Money subtotal) {

        /**
         * {@link OrderResultDto.Item} から {@link Item} を組み立てる。
         *
         * @param item 変換元の注文明細
         * @return 組み立てられた {@link Item}
         */
        public static Item from(final OrderResultDto.Item item) {
            return new Item(item.productId(), item.productName(), item.unitPrice(), item.quantity(), item.subtotal());
        }
    }
}
