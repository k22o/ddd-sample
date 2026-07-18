package com.example.dddsample.presentation.exception;

import com.example.dddsample.domain.exception.CustomerNotFoundException;
import com.example.dddsample.domain.exception.InsufficientStockException;
import com.example.dddsample.domain.exception.OrderNotFoundException;
import com.example.dddsample.domain.exception.PaymentFailedException;
import com.example.dddsample.domain.exception.ProductNotFoundException;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 注文APIで発生するドメイン例外をHTTPレスポンスへ変換するハンドラ。
 */
@RestControllerAdvice
@NullMarked
public class GlobalExceptionHandler {

    /**
     * 対象リソースが存在しない場合に{@code 404 Not Found}を返す。
     *
     * @param ex 発生した例外
     * @return エラー内容を表す {@link ProblemDetail}
     */
    @ExceptionHandler({CustomerNotFoundException.class, ProductNotFoundException.class, OrderNotFoundException.class})
    public ProblemDetail handleNotFound(final RuntimeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * 在庫が不足している場合に{@code 409 Conflict}を返す。
     *
     * @param ex 発生した例外
     * @return エラー内容を表す {@link ProblemDetail}
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ProblemDetail handleInsufficientStock(final InsufficientStockException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * 決済に失敗した場合に{@code 402 Payment Required}を返す。
     *
     * @param ex 発生した例外
     * @return エラー内容を表す {@link ProblemDetail}
     */
    @ExceptionHandler(PaymentFailedException.class)
    public ProblemDetail handlePaymentFailed(final PaymentFailedException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.PAYMENT_REQUIRED, ex.getMessage());
    }

    /**
     * リクエスト内容が不正な場合に{@code 400 Bad Request}を返す。
     *
     * @param ex 発生した例外
     * @return エラー内容を表す {@link ProblemDetail}
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(final IllegalArgumentException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * リクエストボディの入力値がBean Validationの制約に違反する場合に{@code 400 Bad Request}を返す。
     *
     * @param ex 発生した例外
     * @return エラー内容を表す {@link ProblemDetail}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationError(final MethodArgumentNotValidException ex) {
        final Map<String, String> errors = new LinkedHashMap<>();
        for (final FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(fieldError.getField(), Objects.requireNonNullElse(fieldError.getDefaultMessage(), "不正な値です"));
        }
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "リクエストの内容が不正です");
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

    /**
     * パスパラメータ・クエリパラメータの入力値がBean Validationの制約に違反する場合に{@code 400 Bad Request}を返す。
     *
     * @param ex 発生した例外
     * @return エラー内容を表す {@link ProblemDetail}
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ProblemDetail handleMethodValidationError(final HandlerMethodValidationException ex) {
        final String detail = ex.getValueResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream())
                .map(error -> Objects.requireNonNullElse(error.getDefaultMessage(), "不正な値です"))
                .collect(Collectors.joining(", "));
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    }
}
