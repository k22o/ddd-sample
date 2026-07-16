package com.example.dddsample.infrastructure.client;

import com.example.dddsample.domain.exception.InsufficientStockException;
import com.example.dddsample.domain.model.product.ProductId;
import com.example.dddsample.domain.model.shared.Quantity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * {@link InventoryClientImpl} の単体テスト。{@link MockRestServiceServer} で外部在庫管理サービスへのHTTP通信をモック化する。
 */
@SuppressWarnings({"NonAsciiCharacters"})
class InventoryClientImplTest {

    private MockRestServiceServer server;

    private InventoryClientImpl inventoryClient;

    @BeforeEach
    void setUp() {
        final RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        inventoryClient = new InventoryClientImpl(builder.build());
    }

    @AfterEach
    void tearDown() {
        server.verify();
    }

    @Nested
    class CheckStock {

        @Test
        void 在庫が確保可能な場合はtrueを返す() {
            server.expect(requestTo("/inventory/product-1?quantity=2"))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(withSuccess(
                            "{\"productId\":\"product-1\",\"available\":true,\"stock\":10}", MediaType.APPLICATION_JSON));

            final boolean available = inventoryClient.checkStock(new ProductId("product-1"), new Quantity(2));

            assertThat(available).isTrue();
        }

        @Test
        void 在庫が不足している場合はfalseを返す() {
            server.expect(requestTo("/inventory/product-1?quantity=100"))
                    .andRespond(withSuccess(
                            "{\"productId\":\"product-1\",\"available\":false,\"stock\":1}", MediaType.APPLICATION_JSON));

            final boolean available = inventoryClient.checkStock(new ProductId("product-1"), new Quantity(100));

            assertThat(available).isFalse();
        }
    }

    @Nested
    class Reserve {

        @Test
        void 在庫引当に成功すると引当IDを返す() {
            server.expect(requestTo("/inventory/reservations"))
                    .andExpect(method(HttpMethod.POST))
                    .andExpect(jsonPath("$.productId").value("product-1"))
                    .andExpect(jsonPath("$.quantity").value(2))
                    .andRespond(withSuccess(
                            "{\"reservationId\":\"reservation-1\",\"productId\":\"product-1\",\"quantity\":2}",
                            MediaType.APPLICATION_JSON));

            final String reservationId = inventoryClient.reserve(new ProductId("product-1"), new Quantity(2));

            assertThat(reservationId).isEqualTo("reservation-1");
        }

        @Test
        void 在庫管理サービスが在庫不足を返すとInsufficientStockExceptionをスローする() {
            server.expect(requestTo("/inventory/reservations")).andRespond(withServerError());

            assertThatThrownBy(() -> inventoryClient.reserve(new ProductId("product-1"), new Quantity(2)))
                    .isInstanceOf(InsufficientStockException.class);
        }
    }

    @Nested
    class CancelReservation {

        @Test
        void 引当IDを指定してキャンセルリクエストを送信する() {
            server.expect(requestTo("/inventory/reservations/reservation-1"))
                    .andExpect(method(HttpMethod.DELETE))
                    .andRespond(withSuccess());

            inventoryClient.cancelReservation("reservation-1");
        }
    }
}
