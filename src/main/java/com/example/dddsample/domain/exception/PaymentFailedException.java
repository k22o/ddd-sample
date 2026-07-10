package com.example.dddsample.domain.exception;

import org.jspecify.annotations.NullMarked;

/**
 * 外部決済サービスでの決済処理が失敗した場合にスローされる例外。
 */
@NullMarked
public class PaymentFailedException extends RuntimeException {

    /**
     * @param message エラーメッセージ
     */
    public PaymentFailedException(final String message) {
        super(message);
    }
}
