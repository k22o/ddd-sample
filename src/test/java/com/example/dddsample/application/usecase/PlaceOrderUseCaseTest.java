package com.example.dddsample.application.usecase;

import com.example.dddsample.application.dto.OrderItemDto;
import com.example.dddsample.application.dto.OrderResultDto;
import com.example.dddsample.application.dto.PlaceOrderDto;
import com.example.dddsample.domain.exception.CustomerNotFoundException;
import com.example.dddsample.domain.exception.ProductNotFoundException;
import com.example.dddsample.domain.model.customer.Customer;
import com.example.dddsample.domain.model.customer.CustomerId;
import com.example.dddsample.domain.model.order.Order;
import com.example.dddsample.domain.model.product.Product;
import com.example.dddsample.domain.model.product.ProductId;
import com.example.dddsample.domain.model.shared.Address;
import com.example.dddsample.domain.model.shared.Money;
import com.example.dddsample.domain.repository.CustomerRepository;
import com.example.dddsample.domain.repository.OrderRepository;
import com.example.dddsample.domain.repository.ProductRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link PlaceOrderUseCase} の単体テスト。{@link CustomerRepository}・{@link ProductRepository}・{@link OrderRepository} はモック化する。
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"NonAsciiCharacters"})
class PlaceOrderUseCaseTest {

    private static final Address ADDRESS = new Address("100-0001", "東京都", "千代田区", "1-1-1");

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PlaceOrderUseCase placeOrderUseCase;

    private Product product(final String id, final String name, final String price) {
        return new Product(new ProductId(id), name, new Money(new BigDecimal(price), "JPY"), null);
    }

    @Nested
    class Execute {

        @Test
        void 顧客と商品が存在する場合は注文を作成して保存する() {
            final PlaceOrderDto dto = new PlaceOrderDto(
                    "customer-1", ADDRESS, List.of(new OrderItemDto("product-1", 2)));
            when(customerRepository.findById(new CustomerId("customer-1")))
                    .thenReturn(new Customer(new CustomerId("customer-1"), "山田太郎", "yamada@example.com", ADDRESS));
            when(productRepository.findByIds(List.of(new ProductId("product-1"))))
                    .thenReturn(List.of(product("product-1", "商品A", "1000")));

            final OrderResultDto result = placeOrderUseCase.execute(dto);

            assertThat(result.customerId()).isEqualTo("customer-1");
            assertThat(result.items()).hasSize(1);
            assertThat(result.items().get(0).productName()).isEqualTo("商品A");
            assertThat(result.totalAmount()).isEqualTo(new Money(new BigDecimal("2000"), "JPY"));

            final ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
            verify(orderRepository).save(captor.capture());
            assertThat(captor.getValue().customerId()).isEqualTo(new CustomerId("customer-1"));
        }

        @Test
        void 顧客が存在しない場合はCustomerNotFoundExceptionをスローし注文を保存しない() {
            final PlaceOrderDto dto = new PlaceOrderDto(
                    "unknown", ADDRESS, List.of(new OrderItemDto("product-1", 2)));
            when(customerRepository.findById(new CustomerId("unknown")))
                    .thenThrow(new CustomerNotFoundException("unknown"));

            assertThatThrownBy(() -> placeOrderUseCase.execute(dto))
                    .isInstanceOf(CustomerNotFoundException.class);

            verify(orderRepository, never()).save(any());
        }

        @Test
        void 指定した商品が存在しない場合はProductNotFoundExceptionをスローし注文を保存しない() {
            final PlaceOrderDto dto = new PlaceOrderDto(
                    "customer-1", ADDRESS, List.of(new OrderItemDto("product-1", 2)));
            when(customerRepository.findById(new CustomerId("customer-1")))
                    .thenReturn(new Customer(new CustomerId("customer-1"), "山田太郎", "yamada@example.com", ADDRESS));
            when(productRepository.findByIds(List.of(new ProductId("product-1")))).thenReturn(List.of());

            assertThatThrownBy(() -> placeOrderUseCase.execute(dto))
                    .isInstanceOf(ProductNotFoundException.class);

            verify(orderRepository, never()).save(any());
        }
    }
}
