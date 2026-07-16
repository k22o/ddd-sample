package com.example.dddsample.domain.service;

import com.example.dddsample.domain.client.InventoryClient;
import com.example.dddsample.domain.client.PaymentClient;
import com.example.dddsample.domain.exception.InsufficientStockException;
import com.example.dddsample.domain.exception.PaymentFailedException;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link OrderDomainService} の単体テスト。{@link InventoryClient} と {@link PaymentClient} はモック化する。
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"NonAsciiCharacters"})
class OrderDomainServiceTest {

    private static final Address ADDRESS = new Address("100-0001", "東京都", "千代田区", "1-1-1");

    @Mock
    private InventoryClient inventoryClient;

    @Mock
    private PaymentClient paymentClient;

    @InjectMocks
    private OrderDomainService orderDomainService;

    private Order newOrder() {
        final List<OrderItem> items = Stream.of(new String[]{"product-1", "product-2"})
                .map(productId -> new OrderItem(
                        new OrderItemId("item-" + productId),
                        new ProductId(productId),
                        "商品" + productId,
                        new Money(new BigDecimal("1000"), "JPY"),
                        new Quantity(2)))
                .toList();
        return Order.create(new OrderId("order-1"), new CustomerId("customer-1"), items, ADDRESS, LocalDateTime.now());
    }

    @Nested
    class ConfirmOrder {

        @Test
        void 全明細の在庫引当と決済に成功すると注文をCONFIRMEDに遷移する() {
            final Order order = newOrder();
            when(inventoryClient.checkStock(new ProductId("product-1"), new Quantity(2))).thenReturn(true);
            when(inventoryClient.checkStock(new ProductId("product-2"), new Quantity(2))).thenReturn(true);
            when(inventoryClient.reserve(new ProductId("product-1"), new Quantity(2))).thenReturn("reservation-1");
            when(inventoryClient.reserve(new ProductId("product-2"), new Quantity(2))).thenReturn("reservation-2");
            when(paymentClient.charge(new CustomerId("customer-1"), order.totalAmount())).thenReturn("payment-1");

            final Order confirmed = orderDomainService.confirmOrder(order);

            assertThat(confirmed.status()).isEqualTo(OrderStatus.CONFIRMED);
            assertThat(confirmed.paymentId()).isEqualTo("payment-1");
            verify(inventoryClient, never()).cancelReservation(any());
        }

        @Test
        void 在庫が不足している明細があるとInsufficientStockExceptionをスローし引当済み分をキャンセルする() {
            final Order order = newOrder();
            when(inventoryClient.checkStock(new ProductId("product-1"), new Quantity(2))).thenReturn(true);
            when(inventoryClient.reserve(new ProductId("product-1"), new Quantity(2))).thenReturn("reservation-1");
            when(inventoryClient.checkStock(new ProductId("product-2"), new Quantity(2))).thenReturn(false);

            assertThatThrownBy(() -> orderDomainService.confirmOrder(order))
                    .isInstanceOf(InsufficientStockException.class);

            verify(inventoryClient).cancelReservation("reservation-1");
            verify(paymentClient, never()).charge(any(), any());
        }

        @Test
        void 決済に失敗するとPaymentFailedExceptionをスローし引当済み分をキャンセルする() {
            final Order order = newOrder();
            when(inventoryClient.checkStock(new ProductId("product-1"), new Quantity(2))).thenReturn(true);
            when(inventoryClient.reserve(new ProductId("product-1"), new Quantity(2))).thenReturn("reservation-1");
            when(inventoryClient.checkStock(new ProductId("product-2"), new Quantity(2))).thenReturn(true);
            when(inventoryClient.reserve(new ProductId("product-2"), new Quantity(2))).thenReturn("reservation-2");
            when(paymentClient.charge(new CustomerId("customer-1"), order.totalAmount()))
                    .thenThrow(new PaymentFailedException("決済に失敗しました"));

            assertThatThrownBy(() -> orderDomainService.confirmOrder(order))
                    .isInstanceOf(PaymentFailedException.class);

            verify(inventoryClient).cancelReservation("reservation-1");
            verify(inventoryClient).cancelReservation("reservation-2");
        }
    }
}
