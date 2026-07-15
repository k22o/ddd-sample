package com.example.dddsample.infrastructure.client;

import com.example.dddsample.domain.client.InventoryClient;
import com.example.dddsample.domain.exception.InsufficientStockException;
import com.example.dddsample.domain.model.product.ProductId;
import com.example.dddsample.domain.model.shared.Quantity;
import com.example.dddsample.infrastructure.client.dto.InventoryReserveRequest;
import com.example.dddsample.infrastructure.client.dto.InventoryReserveResponse;
import com.example.dddsample.infrastructure.client.dto.InventoryStockResponse;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Objects;

/**
 * {@link InventoryClient} の実装クラス。外部在庫管理サービスへHTTPリクエストを送信する。
 */
@Component
@NullMarked
public class InventoryClientImpl implements InventoryClient {

    private final RestClient restClient;

    // LombokとQualifierの相性の問題で、コンストラクタをちゃんと書く
    public InventoryClientImpl(@Qualifier("inventoryRestClient") final RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * {@inheritDoc}
     *
     * <p>正常応答時は在庫管理サービスが必ずレスポンスボディを返すことを外部APIの契約として前提とする。
     * 異常ステータスは {@link RestClient} のデフォルト挙動により例外がスローされるため、
     * ここで {@code null} が渡された場合は契約違反とみなす。
     */
    @Override
    public boolean checkStock(final ProductId productId, final Quantity quantity) {
        final InventoryStockResponse response = restClient.get()
                .uri("/inventory/{productId}?quantity={quantity}", productId.value(), quantity.value())
                .retrieve()
                .body(InventoryStockResponse.class);

        return Objects.requireNonNull(response, "在庫確認APIのレスポンスボディが空です").available();
    }

    /**
     * {@inheritDoc}
     *
     * <p>正常応答時は在庫管理サービスが必ずレスポンスボディを返すことを外部APIの契約として前提とする。
     *
     * @throws InsufficientStockException 外部在庫管理サービスが在庫不足を返した場合
     */
    @Override
    public String reserve(final ProductId productId, final Quantity quantity) {
        final InventoryReserveRequest request = new InventoryReserveRequest(
                productId.value(),
                quantity.value()
        );

        final InventoryReserveResponse response = restClient.post()
                .uri("/inventory/reservations")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new InsufficientStockException(productId.value());
                })
                .body(InventoryReserveResponse.class);

        return Objects.requireNonNull(response, "在庫予約APIのレスポンスボディが空です").reservationId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelReservation(final String reservationId) {
        restClient.delete()
                .uri("/inventory/reservations/{reservationId}", reservationId)
                .retrieve()
                .toBodilessEntity();
    }
}
