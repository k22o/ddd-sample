package com.example.dddsample.domain.exception;

import org.jspecify.annotations.NullMarked;

/**
 * 指定した顧客が存在しない場合にスローされる例外。
 */
@NullMarked
public class CustomerNotFoundException extends RuntimeException {

    /**
     * @param customerId 見つからなかった顧客ID
     */
    public CustomerNotFoundException(final String customerId) {
        super("顧客が見つかりません: " + customerId);
    }
}
