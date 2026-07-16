package com.example.dddsample.domain.service;

import com.example.dddsample.domain.client.InventoryClient;
import com.example.dddsample.domain.client.PaymentClient;
import com.example.dddsample.domain.exception.InsufficientStockException;
import com.example.dddsample.domain.exception.PaymentFailedException;
import com.example.dddsample.domain.model.order.Order;
import com.example.dddsample.domain.model.order.OrderItem;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 注文確定に伴う、複数の集約・外部サービスにまたがる処理を担当するドメインサービス。
 */
@Service
@RequiredArgsConstructor
@NullMarked
public class OrderDomainService {

    private final InventoryClient inventoryClient;

    private final PaymentClient paymentClient;

    /**
     * 注文を確定する。各注文明細の在庫を確認・引当したうえで決済処理を行い、注文を{@link com.example.dddsample.domain.model.order.OrderStatus#CONFIRMED}
     * に遷移させる。途中で失敗した場合は、それまでに行った在庫引当をキャンセルする。
     *
     * @param order 確定対象の注文（{@link com.example.dddsample.domain.model.order.OrderStatus#PENDING} であること）
     * @return 確定後の注文
     * @throws InsufficientStockException 在庫が不足している場合
     * @throws PaymentFailedException     決済に失敗した場合
     * @throws IllegalStateException      注文のステータスが {@link com.example.dddsample.domain.model.order.OrderStatus#PENDING} でない場合
     */
    public Order confirmOrder(final Order order) {
        final List<String> reservationIds = new ArrayList<>();
        try {
            for (final OrderItem item : order.items()) {
                reservationIds.add(reserve(item));
            }
            final String paymentId = paymentClient.charge(order.customerId(), order.totalAmount());
            return order.confirm(paymentId);
        } catch (final InsufficientStockException | PaymentFailedException ex) {
            reservationIds.forEach(inventoryClient::cancelReservation);
            throw ex;
        }
    }

    private String reserve(final OrderItem item) {
        if (!inventoryClient.checkStock(item.productId(), item.quantity())) {
            throw new InsufficientStockException(item.productId().value());
        }
        return inventoryClient.reserve(item.productId(), item.quantity());
    }
}
