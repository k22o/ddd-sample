package com.example.dddsample.infrastructure.client;

import com.example.dddsample.domain.client.PaymentClient;
import com.example.dddsample.domain.exception.PaymentFailedException;
import com.example.dddsample.domain.model.customer.CustomerId;
import com.example.dddsample.domain.model.shared.Money;
import com.example.dddsample.infrastructure.client.dto.PaymentRequest;
import com.example.dddsample.infrastructure.client.dto.PaymentResponse;
import com.google.common.annotations.VisibleForTesting;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Objects;

/**
 * {@link PaymentClient} の実装クラス。外部決済サービスへHTTPリクエストを送信する。
 */
@Component
@NullMarked
public class PaymentClientImpl implements PaymentClient {

    private final RestClient restClient;

    // LombokとQualifierの相性の問題で、コンストラクタをちゃんと書く
    public PaymentClientImpl(@Qualifier("paymentRestClient") final RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * {@inheritDoc}
     *
     * <p>正常応答時は決済サービスが必ずレスポンスボディを返すことを外部APIの契約として前提とする。
     *
     * @throws PaymentFailedException 外部決済サービスがエラーを返した場合
     */
    @Override
    public String charge(final CustomerId customerId, final Money amount) {
        // データを取得する
        final PaymentResponse response = fetchPayment(customerId, amount);
        // ドメイン (というか必要な型) に変更する
        return toPaymentId(response);
    }

    /**
     * 決済APIを呼び出し、レスポンスボディをそのまま取得する。
     *
     * @param customerId 決済対象の顧客ID
     * @param amount 決済金額
     * @return 決済APIのレスポンス
     * @throws PaymentFailedException 外部決済サービスがエラーを返した場合
     */
    @VisibleForTesting
    PaymentResponse fetchPayment(final CustomerId customerId, final Money amount) {
        final PaymentRequest request = new PaymentRequest(
                customerId.value(),
                amount.amount(),
                amount.currency()
        );

        final PaymentResponse response = restClient.post()
                .uri("/payments")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new PaymentFailedException("決済に失敗しました");
                })
                .body(PaymentResponse.class);

        return Objects.requireNonNull(response, "決済APIのレスポンスボディが空です");
    }

    /**
     * 決済APIのレスポンスを、決済IDへ変換する。
     *
     * @param response 決済APIのレスポンス
     * @return 決済ID
     */
    private String toPaymentId(final PaymentResponse response) {
        return response.paymentId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancel(final String paymentId) {
        restClient.delete()
                .uri("/payments/{paymentId}", paymentId)
                .retrieve()
                .toBodilessEntity();
    }
}
