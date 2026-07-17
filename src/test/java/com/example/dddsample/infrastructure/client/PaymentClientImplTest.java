package com.example.dddsample.infrastructure.client;

import com.example.dddsample.TestUtil;
import com.example.dddsample.domain.exception.PaymentFailedException;
import com.example.dddsample.domain.model.customer.CustomerId;
import com.example.dddsample.domain.model.shared.Money;
import com.example.dddsample.infrastructure.client.dto.PaymentResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * {@link PaymentClientImpl} の単体テスト。{@link MockRestServiceServer} で外部決済サービスへのHTTP通信をモック化する。
 */
@SuppressWarnings({"NonAsciiCharacters"})
class PaymentClientImplTest {

    private static final String RESOURCE_DIR = "infrastructure/client/payment/";

    private MockRestServiceServer server;

    private PaymentClientImpl paymentClient;

    @BeforeEach
    void setUp() {
        final RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        paymentClient = new PaymentClientImpl(builder.build());
    }

    @AfterEach
    void tearDown() {
        server.verify();
    }

    @Nested
    class Charge {

        @Test
        void 決済に成功すると決済IDを返す() {
            server.expect(requestTo("/payments"))
                    .andExpect(method(HttpMethod.POST))
                    .andExpect(jsonPath("$.customerId").value("customer-1"))
                    .andExpect(jsonPath("$.amount").value(2000))
                    .andExpect(jsonPath("$.currency").value("JPY"))
                    .andRespond(withSuccess(
                            TestUtil.readJson(RESOURCE_DIR + "charge-success-response.json"), MediaType.APPLICATION_JSON));

            final String paymentId = paymentClient.charge(
                    new CustomerId("customer-1"), new Money(new BigDecimal("2000"), "JPY"));

            assertThat(paymentId).isEqualTo("payment-1");
        }

        @Test
        void 決済サービスがエラーを返すとPaymentFailedExceptionをスローする() {
            server.expect(requestTo("/payments")).andRespond(withServerError());

            assertThatThrownBy(() -> paymentClient.charge(
                    new CustomerId("customer-1"), new Money(new BigDecimal("2000"), "JPY")))
                    .isInstanceOf(PaymentFailedException.class);
        }
    }

    @Nested
    class FetchPayment {

        @Test
        void 決済APIのレスポンスをそのまま取得する() {
            server.expect(requestTo("/payments"))
                    .andExpect(method(HttpMethod.POST))
                    .andRespond(withSuccess(
                            TestUtil.readJson(RESOURCE_DIR + "charge-success-response.json"), MediaType.APPLICATION_JSON));

            final PaymentResponse response = paymentClient.fetchPayment(
                    new CustomerId("customer-1"), new Money(new BigDecimal("2000"), "JPY"));

            assertThat(response.paymentId()).isEqualTo("payment-1");
            assertThat(response.status()).isEqualTo("SUCCESS");
        }

        @Test
        void 決済サービスがエラーを返すとPaymentFailedExceptionをスローする() {
            server.expect(requestTo("/payments")).andRespond(withServerError());

            assertThatThrownBy(() -> paymentClient.fetchPayment(
                    new CustomerId("customer-1"), new Money(new BigDecimal("2000"), "JPY")))
                    .isInstanceOf(PaymentFailedException.class);
        }
    }

    @Nested
    class Cancel {

        @Test
        void 決済IDを指定してキャンセルリクエストを送信する() {
            server.expect(requestTo("/payments/payment-1"))
                    .andExpect(method(HttpMethod.DELETE))
                    .andRespond(withSuccess());

            paymentClient.cancel("payment-1");
        }
    }
}
