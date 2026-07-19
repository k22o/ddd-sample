package com.example.dddsample.integration;

import com.example.dddsample.domain.model.customer.CustomerId;
import com.example.dddsample.domain.model.order.Order;
import com.example.dddsample.domain.model.order.OrderId;
import com.example.dddsample.domain.model.order.OrderStatus;
import com.example.dddsample.domain.repository.OrderRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UC-1（注文作成）の結合テスト。Controller → UseCase → Repository → H2(実DB) を実際に結線して検証する。
 * 外部API（決済・在庫）は本ユースケースでは呼び出されないためモック化しない。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:place-order-it")
@Transactional
@Sql("/integration/place-order-seed.sql")
@SuppressWarnings({"NonAsciiCharacters"})
class PlaceOrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void 登録済みの顧客と商品を指定すると注文が作成されDBに永続化される() throws Exception {
        final String requestBody = """
                {
                  "customerId": "customer-1",
                  "shippingAddress": {
                    "postalCode": "100-0001",
                    "prefecture": "東京都",
                    "city": "千代田区",
                    "street": "1-1-1"
                  },
                  "items": [
                    { "productId": "product-1", "quantity": 2 }
                  ]
                }
                """;

        final MvcResult result = mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value("customer-1"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount.amount").value(2000))
                .andExpect(jsonPath("$.items[0].productName").value("商品A"))
                .andExpect(header().string("Cache-Control", "private,no-store"))
                .andReturn();

        final String orderId = JsonPath.read(result.getResponse().getContentAsString(), "$.orderId");

        final Order saved = orderRepository.findById(new OrderId(orderId));
        assertThat(saved.customerId()).isEqualTo(new CustomerId("customer-1"));
        assertThat(saved.status()).isEqualTo(OrderStatus.PENDING);
        assertThat(saved.items()).hasSize(1);
        assertThat(saved.items().getFirst().productName()).isEqualTo("商品A");
        assertThat(saved.items().getFirst().unitPrice().amount()).isEqualByComparingTo(new BigDecimal("1000"));
        assertThat(saved.items().getFirst().quantity().value()).isEqualTo(2);
        assertThat(saved.totalAmount().amount()).isEqualByComparingTo(new BigDecimal("2000"));
    }

    @Test
    void 存在しない顧客IDを指定すると404を返しDBには何も保存されない() throws Exception {
        final String requestBody = """
                {
                  "customerId": "unknown-customer",
                  "shippingAddress": {
                    "postalCode": "100-0001",
                    "prefecture": "東京都",
                    "city": "千代田区",
                    "street": "1-1-1"
                  },
                  "items": [
                    { "productId": "product-1", "quantity": 2 }
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());

        final List<Order> orders = orderRepository.findByCustomerId(new CustomerId("unknown-customer"));
        assertThat(orders).isEmpty();
    }

    @Test
    void 存在しない商品IDを指定すると404を返しDBには何も保存されない() throws Exception {
        final String requestBody = """
                {
                  "customerId": "customer-1",
                  "shippingAddress": {
                    "postalCode": "100-0001",
                    "prefecture": "東京都",
                    "city": "千代田区",
                    "street": "1-1-1"
                  },
                  "items": [
                    { "productId": "unknown-product", "quantity": 2 }
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());

        final List<Order> orders = orderRepository.findByCustomerId(new CustomerId("customer-1"));
        assertThat(orders).isEmpty();
    }
}
