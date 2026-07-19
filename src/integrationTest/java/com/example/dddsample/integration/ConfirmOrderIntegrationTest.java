package com.example.dddsample.integration;

import com.example.dddsample.domain.model.order.Order;
import com.example.dddsample.domain.model.order.OrderId;
import com.example.dddsample.domain.model.order.OrderStatus;
import com.example.dddsample.domain.repository.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.convention.TestBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UC-2（注文確定）の結合テスト。Controller → UseCase → DomainService → Repository → H2(実DB) を実際に結線して検証する。
 * 決済・在庫の外部APIは、実際の {@code paymentRestClient}/{@code inventoryRestClient} Bean を
 * {@link MockRestServiceServer} に差し替えることでHTTP層までモック化する。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:confirm-order-it")
@Transactional
@Sql("/integration/order-seed.sql")
@SuppressWarnings({"NonAsciiCharacters"})
class ConfirmOrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @TestBean(name = "paymentRestClient")
    private RestClient paymentRestClient;

    @TestBean(name = "inventoryRestClient")
    private RestClient inventoryRestClient;

    private static MockRestServiceServer paymentServer;

    private static MockRestServiceServer inventoryServer;

    private static RestClient paymentRestClient() {
        final RestClient.Builder builder = RestClient.builder();
        paymentServer = MockRestServiceServer.bindTo(builder).build();
        return builder.build();
    }

    private static RestClient inventoryRestClient() {
        final RestClient.Builder builder = RestClient.builder();
        inventoryServer = MockRestServiceServer.bindTo(builder).build();
        return builder.build();
    }

    /**
     * {@link TestBean} のファクトリメソッドはコンテキスト初期化時に一度だけ実行されクラス内でBeanが共有されるため、
     * {@link MockRestServiceServer} はテストメソッドごとに{@link MockRestServiceServer#reset()}して状態をクリアする。
     */
    @BeforeEach
    void resetMockServers() {
        paymentServer.reset();
        inventoryServer.reset();
    }

    @AfterEach
    void verifyMockServers() {
        paymentServer.verify();
        inventoryServer.verify();
    }

    @Test
    void 在庫確認_引当_決済が成功すると注文がCONFIRMEDになりDBに永続化される() throws Exception {
        inventoryServer.expect(requestTo("/inventory/product-1?quantity=2"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"productId": "product-1", "available": true, "stock": 10}
                        """, MediaType.APPLICATION_JSON));
        inventoryServer.expect(requestTo("/inventory/reservations"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {"reservationId": "reservation-1", "productId": "product-1", "quantity": 2}
                        """, MediaType.APPLICATION_JSON));
        paymentServer.expect(requestTo("/payments"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {"paymentId": "payment-1", "status": "SUCCESS"}
                        """, MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/api/v1/orders/{orderId}/confirm", "order-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("order-1"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.paymentId").value("payment-1"))
                .andExpect(header().string("Cache-Control", "private,no-store"));

        final Order confirmed = orderRepository.findById(new OrderId("order-1"));
        assertThat(confirmed.status()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(confirmed.paymentId()).isEqualTo("payment-1");
    }

    @Test
    void 在庫が不足している場合は409を返しDBの注文はPENDINGのまま() throws Exception {
        inventoryServer.expect(requestTo("/inventory/product-1?quantity=2"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"productId": "product-1", "available": false, "stock": 0}
                        """, MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/api/v1/orders/{orderId}/confirm", "order-1"))
                .andExpect(status().isConflict());

        final Order order = orderRepository.findById(new OrderId("order-1"));
        assertThat(order.status()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void 決済に失敗した場合は402を返しDBの注文はPENDINGのままで引当分は補償キャンセルされる() throws Exception {
        inventoryServer.expect(requestTo("/inventory/product-1?quantity=2"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"productId": "product-1", "available": true, "stock": 10}
                        """, MediaType.APPLICATION_JSON));
        inventoryServer.expect(requestTo("/inventory/reservations"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {"reservationId": "reservation-1", "productId": "product-1", "quantity": 2}
                        """, MediaType.APPLICATION_JSON));
        paymentServer.expect(requestTo("/payments"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());
        inventoryServer.expect(requestTo("/inventory/reservations/reservation-1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());

        mockMvc.perform(post("/api/v1/orders/{orderId}/confirm", "order-1"))
                .andExpect(status().isPaymentRequired());

        final Order order = orderRepository.findById(new OrderId("order-1"));
        assertThat(order.status()).isEqualTo(OrderStatus.PENDING);
    }
}
