package com.example.dddsample.application.dto;

import com.example.dddsample.domain.model.order.Order;
import com.example.dddsample.domain.model.order.OrderItem;
import com.example.dddsample.domain.model.order.OrderStatus;
import com.example.dddsample.domain.model.shared.Address;
import com.example.dddsample.domain.model.shared.Money;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 注文処理結果を表すDto。{@link Order} 集約の内容をユースケース層と表現層の間で受け渡す。
 *
 * @param orderId         注文ID
 * @param customerId      顧客ID
 * @param status          注文ステータス
 * @param shippingAddress 配送先住所
 * @param items           注文明細
 * @param totalAmount     合計金額
 * @param paymentId       外部決済サービスの支払いID（未確定の場合は {@code null}）
 * @param createdAt       注文日時
 */
@NullMarked
public record OrderResultDto(
        String orderId,
        String customerId,
        OrderStatus status,
        Address shippingAddress,
        List<Item> items,
        Money totalAmount,
        @Nullable String paymentId,
        LocalDateTime createdAt) {

    /**
     * {@link Order} 集約から {@link OrderResultDto} を組み立てる。
     *
     * @param order 変換元の注文
     * @return 組み立てられた {@link OrderResultDto}
     */
    public static OrderResultDto from(final Order order) {
        return new OrderResultDto(
                order.id().value(),
                order.customerId().value(),
                order.status(),
                order.shippingAddress(),
                order.items().stream().map(Item::from).toList(),
                order.totalAmount(),
                order.paymentId(),
                order.createdAt());
    }

    /**
     * 注文明細を表すDto。
     *
     * @param productId   商品ID
     * @param productName 注文時点の商品名
     * @param unitPrice   注文時点の単価
     * @param quantity    数量
     * @param subtotal    小計金額
     */
    public record Item(String productId, String productName, Money unitPrice, int quantity, Money subtotal) {

        /**
         * {@link OrderItem} から {@link Item} を組み立てる。
         *
         * @param orderItem 変換元の注文明細
         * @return 組み立てられた {@link Item}
         */
        public static Item from(final OrderItem orderItem) {
            return new Item(
                    orderItem.productId().value(),
                    orderItem.productName(),
                    orderItem.unitPrice(),
                    orderItem.quantity().value(),
                    orderItem.subtotal());
        }
    }
}
