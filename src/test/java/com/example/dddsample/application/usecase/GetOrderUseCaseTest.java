package com.example.dddsample.application.usecase;

import com.example.dddsample.application.dto.OrderResultDto;
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
import com.example.dddsample.domain.repository.OrderRepository;
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
import static org.mockito.Mockito.when;

/**
 * {@link GetOrderUseCase} の単体テスト。{@link OrderRepository} はモック化する。
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"NonAsciiCharacters"})
class GetOrderUseCaseTest {

    private static final Address ADDRESS = new Address("100-0001", "東京都", "千代田区", "1-1-1");

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private GetOrderUseCase getOrderUseCase;

    @Nested
    class Execute {

        @Test
        void 登録済みの注文IDを指定すると明細を含む注文を返す() {
            final List<OrderItem> items = List.of(new OrderItem(
                    new OrderItemId("item-1"),
                    new ProductId("product-1"),
                    "商品A",
                    new Money(new BigDecimal("1000"), "JPY"),
                    new Quantity(2)));
            final Order order = Order.create(
                    new OrderId("order-1"), new CustomerId("customer-1"), items, ADDRESS, LocalDateTime.now());
            when(orderRepository.findById(new OrderId("order-1"))).thenReturn(order);

            final OrderResultDto result = getOrderUseCase.execute("order-1");

            assertThat(result.orderId()).isEqualTo("order-1");
            assertThat(result.items()).hasSize(1);
            assertThat(result.items().get(0).productId()).isEqualTo("product-1");
        }

        @Test
        void 未登録の注文IDを指定するとOrderNotFoundExceptionをスローする() {
            when(orderRepository.findById(new OrderId("unknown"))).thenThrow(new OrderNotFoundException("unknown"));

            assertThatThrownBy(() -> getOrderUseCase.execute("unknown"))
                    .isInstanceOf(OrderNotFoundException.class);
        }
    }
}
