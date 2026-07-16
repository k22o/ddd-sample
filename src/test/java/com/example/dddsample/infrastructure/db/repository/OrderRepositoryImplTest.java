package com.example.dddsample.infrastructure.db.repository;

import com.example.dddsample.domain.exception.OrderNotFoundException;
import com.example.dddsample.domain.model.customer.CustomerId;
import com.example.dddsample.domain.model.order.Order;
import com.example.dddsample.domain.model.order.OrderId;
import com.example.dddsample.domain.model.order.OrderItem;
import com.example.dddsample.domain.model.order.OrderItemId;
import com.example.dddsample.domain.model.product.ProductId;
import com.example.dddsample.domain.model.shared.Address;
import com.example.dddsample.domain.model.shared.Money;
import com.example.dddsample.domain.model.shared.Quantity;
import com.example.dddsample.infrastructure.db.mapper.OrderItemMapper;
import com.example.dddsample.infrastructure.db.mapper.OrderMapper;
import com.example.dddsample.infrastructure.db.record.OrderItemRecord;
import com.example.dddsample.infrastructure.db.record.OrderRecord;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link OrderRepositoryImpl} の単体テスト。{@link OrderMapper} と {@link OrderItemMapper} はモック化する。
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"NonAsciiCharacters"})
class OrderRepositoryImplTest {

    private static final Address ADDRESS = new Address("100-0001", "東京都", "千代田区", "1-1-1");

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    @InjectMocks
    private OrderRepositoryImpl orderRepository;

    private OrderRecord orderRecord(final String id, final String status) {
        return new OrderRecord(
                id, "customer-1", status, new BigDecimal("2000.00"), "JPY",
                "100-0001", "東京都", "千代田区", "1-1-1", null, LocalDateTime.now());
    }

    private OrderItemRecord orderItemRecord(final String id, final String orderId) {
        return new OrderItemRecord(id, orderId, "product-1", "商品A", new BigDecimal("1000.00"), "JPY", 2);
    }

    private Order newOrder(final String orderId, final String... itemIds) {
        final List<OrderItem> items = List.of(itemIds).stream()
                .map(itemId -> new OrderItem(
                        new OrderItemId(itemId),
                        new ProductId("product-1"),
                        "商品A",
                        new Money(new BigDecimal("1000"), "JPY"),
                        new Quantity(2)))
                .toList();
        return Order.create(new OrderId(orderId), new CustomerId("customer-1"), items, ADDRESS, LocalDateTime.now());
    }

    @Nested
    class FindById {

        @Test
        void 登録済みの注文IDを指定すると明細を含む注文を返す() {
            when(orderMapper.findById("order-1")).thenReturn(orderRecord("order-1", "PENDING"));
            when(orderItemMapper.findByOrderId("order-1"))
                    .thenReturn(List.of(orderItemRecord("item-1", "order-1"), orderItemRecord("item-2", "order-1")));

            final Order order = orderRepository.findById(new OrderId("order-1"));

            assertThat(order.id()).isEqualTo(new OrderId("order-1"));
            assertThat(order.items()).hasSize(2);
        }

        @Test
        void 未登録の注文IDを指定するとOrderNotFoundExceptionをスローする() {
            when(orderMapper.findById("unknown")).thenReturn(null);

            assertThatThrownBy(() -> orderRepository.findById(new OrderId("unknown")))
                    .isInstanceOf(OrderNotFoundException.class);

            verify(orderItemMapper, never()).findByOrderId(any());
        }
    }

    @Nested
    class FindByCustomerId {

        @Test
        void 顧客に紐づく複数の注文をそれぞれの明細を正しく紐付けて返す() {
            when(orderMapper.findByCustomerId("customer-1"))
                    .thenReturn(List.of(orderRecord("order-1", "PENDING"), orderRecord("order-2", "PENDING")));
            when(orderItemMapper.findByOrderIds(List.of("order-1", "order-2")))
                    .thenReturn(List.of(
                            orderItemRecord("item-1", "order-1"),
                            orderItemRecord("item-2", "order-1"),
                            orderItemRecord("item-3", "order-2")));

            final List<Order> orders = orderRepository.findByCustomerId(new CustomerId("customer-1"));

            assertThat(orders).hasSize(2);
            final Order order1 = orders.stream().filter(o -> o.id().value().equals("order-1")).findFirst()
                    .orElseThrow();
            final Order order2 = orders.stream().filter(o -> o.id().value().equals("order-2")).findFirst()
                    .orElseThrow();
            assertThat(order1.items()).extracting(item -> item.id().value())
                    .containsExactlyInAnyOrder("item-1", "item-2");
            assertThat(order2.items()).extracting(item -> item.id().value())
                    .containsExactly("item-3");
        }

        @Test
        void 紐づく注文が無い場合はマッパーを呼び出さずに空リストを返す() {
            when(orderMapper.findByCustomerId("customer-1")).thenReturn(List.of());

            final List<Order> orders = orderRepository.findByCustomerId(new CustomerId("customer-1"));

            assertThat(orders).isEmpty();
            verify(orderItemMapper, never()).findByOrderIds(any());
        }
    }

    @Nested
    class Save {

        @Test
        void 未登録の注文IDであれば明細を含めて新規登録する() {
            final Order order = newOrder("order-1", "item-1", "item-2");

            orderRepository.save(order);

            verify(orderMapper).insert(any());
            @SuppressWarnings("unchecked")
            final ArgumentCaptor<List<OrderItemRecord>> captor = ArgumentCaptor.forClass(List.class);
            verify(orderItemMapper).insertAll(captor.capture());
            assertThat(captor.getValue()).hasSize(2);
            verify(orderMapper, never()).updateStatusAndPayment(any(), any(), any());
        }

        @Test
        void 登録済みの注文IDであれば一意制約違反を検知してステータスと支払いIDのみ更新する() {
            final Order confirmedOrder = newOrder("order-1", "item-1").confirm("payment-1");
            when(orderMapper.insert(any())).thenThrow(new DuplicateKeyException("duplicate"));

            orderRepository.save(confirmedOrder);

            verify(orderMapper).updateStatusAndPayment("order-1", "CONFIRMED", "payment-1");
            verify(orderItemMapper, never()).insertAll(any());
        }
    }
}
