package com.example.dddsample.infrastructure.db.repository;

import com.example.dddsample.domain.exception.OrderNotFoundException;
import com.example.dddsample.domain.model.customer.CustomerId;
import com.example.dddsample.domain.model.order.Order;
import com.example.dddsample.domain.model.order.OrderId;
import com.example.dddsample.domain.model.order.OrderItem;
import com.example.dddsample.domain.model.order.OrderItemId;
import com.example.dddsample.domain.model.order.OrderStatus;
import com.example.dddsample.domain.model.product.ProductId;
import com.example.dddsample.domain.model.shared.Address;
import com.example.dddsample.domain.model.shared.Money;
import com.example.dddsample.domain.model.shared.Quantity;
import com.example.dddsample.domain.repository.OrderRepository;
import com.example.dddsample.infrastructure.db.mapper.OrderItemMapper;
import com.example.dddsample.infrastructure.db.mapper.OrderMapper;
import com.example.dddsample.infrastructure.db.record.OrderItemRecord;
import com.example.dddsample.infrastructure.db.record.OrderRecord;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@link OrderRepository} の実装クラス。{@code orders} と {@code order_items} の2テーブルを
 * {@link Order} 集約として組み立てる。
 */
@Repository
@RequiredArgsConstructor
@NullMarked
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderMapper orderMapper;

    private final OrderItemMapper orderItemMapper;

    /**
     * {@inheritDoc}
     *
     * @throws OrderNotFoundException 注文が存在しない場合
     */
    @Override
    public Order findById(final OrderId id) {
        final OrderRecord record = orderMapper.findById(id.value());
        if (record == null) {
            throw new OrderNotFoundException(id.value());
        }
        return toDomain(record, orderItemMapper.findByOrderId(record.id()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Order> findByCustomerId(final CustomerId customerId) {
        final List<OrderRecord> orderRecords = orderMapper.findByCustomerId(customerId.value());
        if (orderRecords.isEmpty()) {
            return List.of();
        }
        final List<String> orderIds = orderRecords.stream().map(OrderRecord::id).toList();
        final Map<String, List<OrderItemRecord>> itemRecordsByOrderId =
                orderItemMapper.findByOrderIds(orderIds).stream()
                        .collect(Collectors.groupingBy(OrderItemRecord::orderId));
        return orderRecords.stream()
                .map(record -> toDomain(
                        record, itemRecordsByOrderId.getOrDefault(record.id(), List.of())))
                .toList();
    }

    /**
     * {@inheritDoc}
     *
     * <p>注文IDでの新規登録を試み、一意制約違反（登録済み）であればステータスと支払いIDのみ更新する
     * （注文明細は作成時から変更されない前提）。事前の存在確認を行わないことで、確認と登録の間に
     * 割り込みが発生する競合状態（TOCTOU）を避けている。
     */
    @Override
    @Transactional
    public void save(final Order order) {
        try {
            orderMapper.insert(toOrderRecord(order));
            orderItemMapper.insertAll(
                    order.items().stream().map(item -> toOrderItemRecord(order.id(), item)).toList());
        } catch (final DuplicateKeyException e) {
            orderMapper.updateStatusAndPayment(order.id().value(), order.status().name(), order.paymentId());
        }
    }

    /**
     * 注文ヘッダー・明細のレコードをドメインモデルに変換する。
     *
     * @param record       注文ヘッダーのレコード
     * @param itemRecords  注文明細のレコード一覧
     * @return 変換後の {@link Order}
     */
    private Order toDomain(final OrderRecord record, final List<OrderItemRecord> itemRecords) {
        final List<OrderItem> items = itemRecords.stream().map(this::toDomain).toList();
        return new Order(
                new OrderId(record.id()),
                new CustomerId(record.customerId()),
                OrderStatus.valueOf(record.status()),
                items,
                new Money(record.totalAmount(), record.totalCurrency()),
                new Address(record.postalCode(), record.prefecture(), record.city(), record.street()),
                record.paymentId(),
                record.createdAt());
    }

    /**
     * 注文明細のレコードをドメインモデルに変換する。
     *
     * @param record 変換対象のレコード
     * @return 変換後の {@link OrderItem}
     */
    private OrderItem toDomain(final OrderItemRecord record) {
        return new OrderItem(
                new OrderItemId(record.id()),
                new ProductId(record.productId()),
                record.productName(),
                new Money(record.unitPriceAmount(), record.unitPriceCurrency()),
                new Quantity(record.quantity()));
    }

    /**
     * 注文ヘッダーをレコードに変換する。
     *
     * @param order 変換対象の注文
     * @return 変換後の {@link OrderRecord}
     */
    private OrderRecord toOrderRecord(final Order order) {
        return new OrderRecord(
                order.id().value(),
                order.customerId().value(),
                order.status().name(),
                order.totalAmount().amount(),
                order.totalAmount().currency(),
                order.shippingAddress().postalCode(),
                order.shippingAddress().prefecture(),
                order.shippingAddress().city(),
                order.shippingAddress().street(),
                order.paymentId(),
                order.createdAt());
    }

    /**
     * 注文明細をレコードに変換する。
     *
     * @param orderId 紐づく注文ID
     * @param item    変換対象の注文明細
     * @return 変換後の {@link OrderItemRecord}
     */
    private OrderItemRecord toOrderItemRecord(final OrderId orderId, final OrderItem item) {
        return new OrderItemRecord(
                item.id().value(),
                orderId.value(),
                item.productId().value(),
                item.productName(),
                item.unitPrice().amount(),
                item.unitPrice().currency(),
                item.quantity().value());
    }
}
