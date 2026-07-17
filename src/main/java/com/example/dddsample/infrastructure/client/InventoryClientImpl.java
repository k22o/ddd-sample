package com.example.dddsample.infrastructure.client;

import com.example.dddsample.domain.client.InventoryClient;
import com.example.dddsample.domain.exception.InsufficientStockException;
import com.example.dddsample.domain.model.product.ProductId;
import com.example.dddsample.domain.model.shared.Quantity;
import com.example.dddsample.infrastructure.client.dto.InventoryReserveRequest;
import com.example.dddsample.infrastructure.client.dto.InventoryReserveResponse;
import com.example.dddsample.infrastructure.client.dto.InventoryStockResponse;
import com.google.common.annotations.VisibleForTesting;
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
        // データを取得する
        final InventoryStockResponse response = fetchStock(productId, quantity);
        // ドメイン (というか必要な型) に変更する
        return toAvailable(response);
    }

    //
    /**
     * 在庫確認APIを呼び出し、レスポンスボディをそのまま取得する。
     *
     * @param productId 確認対象の商品ID
     * @param quantity 確認対象の数量
     * @return 在庫確認APIのレスポンス
     */
    @VisibleForTesting
    InventoryStockResponse fetchStock(final ProductId productId, final Quantity quantity) {
        final InventoryStockResponse response = restClient.get()
                .uri("/inventory/{productId}?quantity={quantity}", productId.value(), quantity.value())
                .retrieve()
                .body(InventoryStockResponse.class);

        return Objects.requireNonNull(response, "在庫確認APIのレスポンスボディが空です");
    }

    /**
     * 在庫確認APIのレスポンスを、引当可否のフラグへ変換する。
     *
     * @param response 在庫確認APIのレスポンス
     * @return 引当可能であれば{@code true}
     */
    private boolean toAvailable(final InventoryStockResponse response) {
        return response.available();
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
        // データを取得する
        final InventoryReserveResponse response = fetchReservation(productId, quantity);
        // ドメイン (というか必要な型) に変更する
        return toReservationId(response);
    }

    /**
     * 在庫予約APIを呼び出し、レスポンスボディをそのまま取得する。
     *
     * @param productId 予約対象の商品ID
     * @param quantity 予約対象の数量
     * @return 在庫予約APIのレスポンス
     * @throws InsufficientStockException 外部在庫管理サービスが在庫不足を返した場合
     */
    @VisibleForTesting
    InventoryReserveResponse fetchReservation(final ProductId productId, final Quantity quantity) {
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

        return Objects.requireNonNull(response, "在庫予約APIのレスポンスボディが空です");
    }

    /**
     * 在庫予約APIのレスポンスを、引当IDへ変換する。
     *
     * @param response 在庫予約APIのレスポンス
     * @return 引当ID
     */
    private String toReservationId(final InventoryReserveResponse response) {
        return response.reservationId();
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
