package com.example.dddsample.application.usecase;

import com.example.dddsample.application.dto.OrderResultDto;
import com.example.dddsample.domain.model.order.Order;
import com.example.dddsample.domain.model.order.OrderId;
import com.example.dddsample.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;

/**
 * UC-3: 注文取得のユースケース。
 */
@Service
@RequiredArgsConstructor
@NullMarked
public class GetOrderUseCase {

    private final OrderRepository orderRepository;

    /**
     * 注文IDで注文を取得する。
     *
     * @param orderId 注文ID
     * @return 注文
     * @throws com.example.dddsample.domain.exception.OrderNotFoundException 注文が存在しない場合
     */
    public OrderResultDto execute(final String orderId) {
        final Order order = orderRepository.findById(new OrderId(orderId));
        return OrderResultDto.from(order);
    }
}
