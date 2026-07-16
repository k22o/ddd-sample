package com.example.dddsample.application.usecase;

import com.example.dddsample.application.dto.OrderResultDto;
import com.example.dddsample.domain.exception.InsufficientStockException;
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
import com.example.dddsample.domain.service.OrderDomainService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
 * {@link ConfirmOrderUseCase} の単体テスト。{@link OrderRepository} と {@link OrderDomainService} はモック化する。
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"NonAsciiCharacters"})
class ConfirmOrderUseCaseTest {

    private static final Address ADDRESS = new Address("100-0001", "東京都", "千代田区", "1-1-1");

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderDomainService orderDomainService;

    @InjectMocks
    private ConfirmOrderUseCase confirmOrderUseCase;

    private Order pendingOrder() {
        final List<OrderItem> items = List.of(new OrderItem(
                new OrderItemId("item-1"),
                new ProductId("product-1"),
                "商品A",
                new Money(new BigDecimal("1000"), "JPY"),
                new Quantity(2)));
        return Order.create(new OrderId("order-1"), new CustomerId("customer-1"), items, ADDRESS, LocalDateTime.now());
    }

    @Nested
    class Execute {

        @Test
        void 在庫引当と決済に成功すると注文を確定して保存する() {
            final Order order = pendingOrder();
            final Order confirmedOrder = order.confirm("payment-1");
            when(orderRepository.findById(new OrderId("order-1"))).thenReturn(order);
            when(orderDomainService.confirmOrder(order)).thenReturn(confirmedOrder);

            final OrderResultDto result = confirmOrderUseCase.execute("order-1");

            assertThat(result.status()).isEqualTo(OrderStatus.CONFIRMED);
            assertThat(result.paymentId()).isEqualTo("payment-1");
            verify(orderRepository).save(confirmedOrder);
        }

        @Test
        void 在庫が不足している場合はInsufficientStockExceptionをスローし注文を保存しない() {
            final Order order = pendingOrder();
            when(orderRepository.findById(new OrderId("order-1"))).thenReturn(order);
            when(orderDomainService.confirmOrder(order)).thenThrow(new InsufficientStockException("product-1"));

            assertThatThrownBy(() -> confirmOrderUseCase.execute("order-1"))
                    .isInstanceOf(InsufficientStockException.class);

            verify(orderRepository, never()).save(any());
        }
    }
}
