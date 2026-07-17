package com.example.dddsample.presentation.response;

import com.example.dddsample.application.dto.OrderResultDto;
import com.example.dddsample.domain.model.order.OrderStatus;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * 注文確定APIのレスポンスボディ。
 *
 * @param orderId   注文ID
 * @param status    注文ステータス
 * @param paymentId 外部決済サービスの支払いID
 */
@NullMarked
public record OrderConfirmResponse(String orderId, OrderStatus status, @Nullable String paymentId) {

    /**
     * {@link OrderResultDto} から {@link OrderConfirmResponse} を組み立てる。
     *
     * @param dto 変換元の注文処理結果
     * @return 組み立てられた {@link OrderConfirmResponse}
     */
    public static OrderConfirmResponse from(final OrderResultDto dto) {
        return new OrderConfirmResponse(dto.orderId(), dto.status(), dto.paymentId());
    }
}
