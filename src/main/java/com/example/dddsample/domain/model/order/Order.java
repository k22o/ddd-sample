package com.example.dddsample.domain.model.order;

import com.example.dddsample.domain.model.customer.CustomerId;
import com.example.dddsample.domain.model.shared.Address;
import com.example.dddsample.domain.model.shared.Money;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 注文を表す集約ルート。
 *
 * @param id               注文ID
 * @param customerId       顧客ID
 * @param status           注文ステータス
 * @param items            注文明細（1件以上必須）
 * @param totalAmount      合計金額
 * @param shippingAddress  配送先住所
 * @param paymentId        外部決済サービスの支払いID（未確定の場合は {@code null}）
 * @param createdAt        注文日時
 */
@NullMarked
public record Order(
        OrderId id,
        CustomerId customerId,
        OrderStatus status,
        List<OrderItem> items,
        Money totalAmount,
        Address shippingAddress,
        @Nullable String paymentId,
        LocalDateTime createdAt) {

    /**
     * @throws IllegalArgumentException 注文明細が1件もない場合
     */
    public Order {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("注文明細は1件以上必須です");
        }
        items = List.copyOf(items);
    }

    /**
     * 新規注文を作成する。ステータスは {@link OrderStatus#PENDING}、合計金額は明細から自動計算される。
     *
     * @param id              注文ID
     * @param customerId      顧客ID
     * @param items           注文明細
     * @param shippingAddress 配送先住所
     * @param createdAt       注文日時
     * @return 作成された {@link Order}
     * @throws IllegalArgumentException 注文明細が1件もない場合
     */
    public static Order create(
            final OrderId id,
            final CustomerId customerId,
            final List<OrderItem> items,
            final Address shippingAddress,
            final LocalDateTime createdAt) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("注文明細は1件以上必須です");
        }
        final Money totalAmount = items.stream()
                .map(OrderItem::subtotal)
                .reduce(Money::add)
                .orElseThrow();
        return new Order(id, customerId, OrderStatus.PENDING, items, totalAmount, shippingAddress, null, createdAt);
    }

    /**
     * 在庫確認・決済処理の完了を受けて注文を確定する。
     *
     * @param paymentId 外部決済サービスの支払いID
     * @return 確定後の {@link Order}
     * @throws IllegalStateException ステータスが {@link OrderStatus#PENDING} でない場合
     */
    public Order confirm(final String paymentId) {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("PENDING状態の注文のみ確定できます: " + status);
        }
        return new Order(id, customerId, OrderStatus.CONFIRMED, items, totalAmount, shippingAddress, paymentId, createdAt);
    }

    /**
     * 注文をキャンセルする。
     *
     * @return キャンセル後の {@link Order}
     * @throws IllegalStateException ステータスが {@link OrderStatus#CONFIRMED} または {@link OrderStatus#CANCELLED} の場合
     */
    public Order cancel() {
        if (status == OrderStatus.CONFIRMED) {
            throw new IllegalStateException("確定済みの注文はキャンセルできません");
        }
        if (status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("すでにキャンセル済みの注文です");
        }
        return new Order(id, customerId, OrderStatus.CANCELLED, items, totalAmount, shippingAddress, paymentId, createdAt);
    }
}
