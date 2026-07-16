package com.example.dddsample.application.usecase;

import com.example.dddsample.application.dto.OrderResultDto;
import com.example.dddsample.domain.model.order.Order;
import com.example.dddsample.domain.model.order.OrderId;
import com.example.dddsample.domain.repository.OrderRepository;
import com.example.dddsample.domain.service.OrderDomainService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;

/**
 * UC-2: 注文確定のユースケース。
 */
@Service
@RequiredArgsConstructor
@NullMarked
public class ConfirmOrderUseCase {

    private final OrderRepository orderRepository;

    private final OrderDomainService orderDomainService;

    /**
     * 注文を確定する。在庫確認・引当と決済処理を行ったうえで、注文のステータスを{@link com.example.dddsample.domain.model.order.OrderStatus#CONFIRMED}に更新する。
     *
     * @param orderId 確定対象の注文ID
     * @return 確定後の注文
     * @throws com.example.dddsample.domain.exception.OrderNotFoundException       注文が存在しない場合
     * @throws com.example.dddsample.domain.exception.InsufficientStockException  在庫が不足している場合
     * @throws com.example.dddsample.domain.exception.PaymentFailedException      決済に失敗した場合
     */
    public OrderResultDto execute(final String orderId) {
        final Order order = orderRepository.findById(new OrderId(orderId));
        final Order confirmedOrder = orderDomainService.confirmOrder(order);
        orderRepository.save(confirmedOrder);
        return OrderResultDto.from(confirmedOrder);
    }
}
