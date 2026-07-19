package com.example.dddsample.presentation.controller;

import com.example.dddsample.application.dto.OrderResultDto;
import com.example.dddsample.application.dto.PlaceOrderDto;
import com.example.dddsample.application.usecase.ConfirmOrderUseCase;
import com.example.dddsample.application.usecase.GetOrderUseCase;
import com.example.dddsample.application.usecase.PlaceOrderUseCase;
import com.example.dddsample.domain.exception.InsufficientStockException;
import com.example.dddsample.domain.exception.OrderNotFoundException;
import com.example.dddsample.domain.model.order.OrderStatus;
import com.example.dddsample.domain.model.shared.Address;
import com.example.dddsample.domain.model.shared.Money;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link OrderController} の単体テスト。{@link PlaceOrderUseCase} などのユースケースはモック化する。
 */
@WebMvcTest(OrderController.class)
@SuppressWarnings({"NonAsciiCharacters"})
class OrderControllerTest {

    private static final Address ADDRESS = new Address("100-0001", "東京都", "千代田区", "1-1-1");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlaceOrderUseCase placeOrderUseCase;

    @MockitoBean
    private ConfirmOrderUseCase confirmOrderUseCase;

    @MockitoBean
    private GetOrderUseCase getOrderUseCase;

    private OrderResultDto pendingOrderResult() {
        final Money unitPrice = new Money(new BigDecimal("1000"), "JPY");
        final OrderResultDto.Item item = new OrderResultDto.Item("product-1", "商品A", unitPrice, 2, unitPrice.multiply(2));
        return new OrderResultDto(
                "order-1",
                "customer-1",
                OrderStatus.PENDING,
                ADDRESS,
                List.of(item),
                unitPrice.multiply(2),
                null,
                LocalDateTime.of(2026, 7, 16, 10, 0));
    }

    @Nested
    class PlaceOrder {

        @Test
        void 注文作成に成功すると201で注文情報を返す() throws Exception {
            when(placeOrderUseCase.execute(any(PlaceOrderDto.class))).thenReturn(pendingOrderResult());

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

            mockMvc.perform(post("/api/v1/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.orderId").value("order-1"))
                    .andExpect(jsonPath("$.status").value("PENDING"))
                    .andExpect(jsonPath("$.totalAmount.amount").value(2000))
                    .andExpect(jsonPath("$.items[0].productName").value("商品A"));
        }

        @Test
        void 注文明細が空の場合は400を返す() throws Exception {
            final String requestBody = """
                    {
                      "customerId": "customer-1",
                      "shippingAddress": {
                        "postalCode": "100-0001",
                        "prefecture": "東京都",
                        "city": "千代田区",
                        "street": "1-1-1"
                      },
                      "items": []
                    }
                    """;

            mockMvc.perform(post("/api/v1/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class ConfirmOrder {

        @Test
        void 注文確定に成功すると200で確定結果を返す() throws Exception {
            final OrderResultDto confirmed = new OrderResultDto(
                    "order-1",
                    "customer-1",
                    OrderStatus.CONFIRMED,
                    ADDRESS,
                    List.of(),
                    new Money(new BigDecimal("2000"), "JPY"),
                    "payment-1",
                    LocalDateTime.now());
            when(confirmOrderUseCase.execute(eq("order-1"))).thenReturn(confirmed);

            mockMvc.perform(post("/api/v1/orders/{orderId}/confirm", "order-1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.orderId").value("order-1"))
                    .andExpect(jsonPath("$.status").value("CONFIRMED"))
                    .andExpect(jsonPath("$.paymentId").value("payment-1"));
        }

        @Test
        void 在庫が不足している場合は409を返す() throws Exception {
            when(confirmOrderUseCase.execute(eq("order-1")))
                    .thenThrow(new InsufficientStockException("product-1"));

            mockMvc.perform(post("/api/v1/orders/{orderId}/confirm", "order-1"))
                    .andExpect(status().isConflict());
        }

        @Test
        void 注文IDが空白の場合は400を返す() throws Exception {
            mockMvc.perform(post("/api/v1/orders/{orderId}/confirm", " "))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class GetOrder {

        @Test
        void 注文取得に成功すると200で注文情報を返す() throws Exception {
            when(getOrderUseCase.execute(eq("order-1"))).thenReturn(pendingOrderResult());

            mockMvc.perform(get("/api/v1/orders/{orderId}", "order-1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.orderId").value("order-1"))
                    .andExpect(jsonPath("$.customerId").value("customer-1"))
                    .andExpect(header().string("Cache-Control", "max-age=30, private"));
        }

        @Test
        void 注文が存在しない場合は404を返す() throws Exception {
            when(getOrderUseCase.execute(eq("unknown"))).thenThrow(new OrderNotFoundException("unknown"));

            mockMvc.perform(get("/api/v1/orders/{orderId}", "unknown"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void 注文IDが空白の場合は400を返す() throws Exception {
            mockMvc.perform(get("/api/v1/orders/{orderId}", " "))
                    .andExpect(status().isBadRequest());
        }
    }
}
