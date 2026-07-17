package com.example.dddsample.presentation.exception;

import com.example.dddsample.domain.exception.CustomerNotFoundException;
import com.example.dddsample.domain.exception.InsufficientStockException;
import com.example.dddsample.domain.exception.OrderNotFoundException;
import com.example.dddsample.domain.exception.PaymentFailedException;
import com.example.dddsample.domain.exception.ProductNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link GlobalExceptionHandler} の単体テスト。
 */
@SuppressWarnings({"NonAsciiCharacters"})
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Nested
    class HandleNotFound {

        @Test
        void CustomerNotFoundExceptionを404に変換する() {
            final ProblemDetail problemDetail = handler.handleNotFound(new CustomerNotFoundException("customer-1"));

            assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(problemDetail.getDetail()).contains("customer-1");
        }

        @Test
        void ProductNotFoundExceptionを404に変換する() {
            final ProblemDetail problemDetail = handler.handleNotFound(new ProductNotFoundException("product-1"));

            assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(problemDetail.getDetail()).contains("product-1");
        }

        @Test
        void OrderNotFoundExceptionを404に変換する() {
            final ProblemDetail problemDetail = handler.handleNotFound(new OrderNotFoundException("order-1"));

            assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(problemDetail.getDetail()).contains("order-1");
        }
    }

    @Nested
    class HandleInsufficientStock {

        @Test
        void InsufficientStockExceptionを409に変換する() {
            final ProblemDetail problemDetail =
                    handler.handleInsufficientStock(new InsufficientStockException("product-1"));

            assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
            assertThat(problemDetail.getDetail()).contains("product-1");
        }
    }

    @Nested
    class HandlePaymentFailed {

        @Test
        void PaymentFailedExceptionを402に変換する() {
            final ProblemDetail problemDetail = handler.handlePaymentFailed(new PaymentFailedException("決済に失敗しました"));

            assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.PAYMENT_REQUIRED.value());
            assertThat(problemDetail.getDetail()).isEqualTo("決済に失敗しました");
        }
    }

    @Nested
    class HandleIllegalArgument {

        @Test
        void IllegalArgumentExceptionを400に変換する() {
            final ProblemDetail problemDetail = handler.handleIllegalArgument(new IllegalArgumentException("不正な値です"));

            assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(problemDetail.getDetail()).isEqualTo("不正な値です");
        }
    }
}
