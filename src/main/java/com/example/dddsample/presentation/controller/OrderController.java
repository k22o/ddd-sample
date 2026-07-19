package com.example.dddsample.presentation.controller;

import com.example.dddsample.application.dto.OrderResultDto;
import com.example.dddsample.application.usecase.ConfirmOrderUseCase;
import com.example.dddsample.application.usecase.GetOrderUseCase;
import com.example.dddsample.application.usecase.PlaceOrderUseCase;
import com.example.dddsample.presentation.request.PlaceOrderRequest;
import com.example.dddsample.presentation.response.OrderConfirmResponse;
import com.example.dddsample.presentation.response.OrderResponse;
import java.util.concurrent.TimeUnit;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 注文APIのコントローラ。
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@NullMarked
public class OrderController {

    private final PlaceOrderUseCase placeOrderUseCase;

    private final ConfirmOrderUseCase confirmOrderUseCase;

    private final GetOrderUseCase getOrderUseCase;

    /**
     * 注文を作成する（UC-1）。
     *
     * @param request 注文作成のリクエストボディ
     * @return 作成された注文（{@code 201 Created}）
     */
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody final PlaceOrderRequest request) {
        final OrderResultDto result = placeOrderUseCase.execute(request.toDto());
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(result));
    }

    /**
     * 注文を確定する（UC-2）。在庫確認・引当と決済処理を行ったうえでステータスを更新する。
     *
     * @param orderId 確定対象の注文ID
     * @return 確定後の注文
     */
    @PostMapping("/{orderId}/confirm")
    public OrderConfirmResponse confirmOrder(
            @PathVariable @NotBlank(message = "注文IDは必須です") final String orderId) {
        final OrderResultDto result = confirmOrderUseCase.execute(orderId);
        return OrderConfirmResponse.from(result);
    }

    /**
     * 注文を取得する（UC-3）。短時間のキャッシュを許容するため、{@code Cache-Control}に{@code max-age=30}を設定する。
     *
     * @param orderId 取得対象の注文ID
     * @return 注文
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable @NotBlank(message = "注文IDは必須です") final String orderId) {
        final OrderResultDto result = getOrderUseCase.execute(orderId);
        return ResponseEntity.ok()
                // 本当はprivate no-storeがいいが、サンプル実装として、cache-controlを上書きしている
                .cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS).cachePrivate())
                .body(OrderResponse.from(result));
    }
}
