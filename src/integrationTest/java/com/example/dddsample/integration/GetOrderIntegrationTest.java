package com.example.dddsample.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UC-3（注文取得）の結合テスト。Controller → UseCase → Repository → H2(実DB) を実際に結線して検証する。
 * 外部API（決済・在庫）は本ユースケースでは呼び出されないためモック化しない。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:get-order-it")
@Transactional
@Sql("/integration/order-seed.sql")
@SuppressWarnings({"NonAsciiCharacters"})
class GetOrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 登録済みの注文IDを指定するとDBの内容が200で返る() throws Exception {
        mockMvc.perform(get("/api/v1/orders/{orderId}", "order-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("order-1"))
                .andExpect(jsonPath("$.customerId").value("customer-1"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount.amount").value(2000))
                .andExpect(jsonPath("$.items[0].productName").value("商品A"))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    @Test
    void 存在しない注文IDを指定すると404を返す() throws Exception {
        mockMvc.perform(get("/api/v1/orders/{orderId}", "unknown-order"))
                .andExpect(status().isNotFound());
    }
}
