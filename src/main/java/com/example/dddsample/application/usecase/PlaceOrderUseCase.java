package com.example.dddsample.application.usecase;

import com.example.dddsample.application.dto.OrderItemDto;
import com.example.dddsample.application.dto.OrderResultDto;
import com.example.dddsample.application.dto.PlaceOrderDto;
import com.example.dddsample.domain.exception.ProductNotFoundException;
import com.example.dddsample.domain.model.customer.CustomerId;
import com.example.dddsample.domain.model.order.Order;
import com.example.dddsample.domain.model.order.OrderId;
import com.example.dddsample.domain.model.order.OrderItem;
import com.example.dddsample.domain.model.order.OrderItemId;
import com.example.dddsample.domain.model.product.Product;
import com.example.dddsample.domain.model.product.ProductId;
import com.example.dddsample.domain.model.shared.Quantity;
import com.example.dddsample.domain.repository.CustomerRepository;
import com.example.dddsample.domain.repository.OrderRepository;
import com.example.dddsample.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * UC-1: 注文作成のユースケース。
 */
@Service
@RequiredArgsConstructor
@NullMarked
public class PlaceOrderUseCase {

    private final CustomerRepository customerRepository;

    private final ProductRepository productRepository;

    private final OrderRepository orderRepository;

    /**
     * 注文を作成する。
     *
     * @param dto 注文作成の入力
     * @return 作成された注文
     * @throws com.example.dddsample.domain.exception.CustomerNotFoundException 顧客が存在しない場合
     * @throws ProductNotFoundException                                        指定した商品が存在しない場合
     */
    public OrderResultDto execute(final PlaceOrderDto dto) {
        final CustomerId customerId = new CustomerId(dto.customerId());
        customerRepository.findById(customerId);

        final List<ProductId> productIds = dto.items().stream().map(item -> new ProductId(item.productId())).toList();
        final Map<ProductId, Product> products = productRepository.findByIds(productIds).stream()
                .collect(Collectors.toMap(Product::id, Function.identity()));

        final List<OrderItem> orderItems = dto.items().stream().map(item -> toOrderItem(item, products)).toList();

        final Order order = Order.create(
                new OrderId(UUID.randomUUID().toString()),
                customerId,
                orderItems,
                dto.shippingAddress(),
                LocalDateTime.now());

        orderRepository.save(order);

        return OrderResultDto.from(order);
    }

    private OrderItem toOrderItem(final OrderItemDto itemDto, final Map<ProductId, Product> products) {
        final ProductId productId = new ProductId(itemDto.productId());
        final Product product = products.get(productId);
        if (product == null) {
            throw new ProductNotFoundException(productId.value());
        }
        return new OrderItem(
                new OrderItemId(UUID.randomUUID().toString()),
                productId,
                product.name(),
                product.price(),
                new Quantity(itemDto.quantity()));
    }
}
