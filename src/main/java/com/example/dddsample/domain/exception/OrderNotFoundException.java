package com.example.dddsample.domain.exception;

import org.jspecify.annotations.NullMarked;

/**
 * 指定した注文が存在しない場合にスローされる例外。
 */
@NullMarked
public class OrderNotFoundException extends RuntimeException {

    /**
     * @param orderId 見つからなかった注文ID
     */
    public OrderNotFoundException(final String orderId) {
        super("注文が見つかりません: " + orderId);
    }
}
