package com.example.dddsample.domain.exception;

import org.jspecify.annotations.NullMarked;

/**
 * 在庫が不足している場合にスローされる例外。
 */
@NullMarked
public class InsufficientStockException extends RuntimeException {

    /**
     * @param productId 在庫不足の商品ID
     */
    public InsufficientStockException(final String productId) {
        super("在庫が不足しています: " + productId);
    }
}
