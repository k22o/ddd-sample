package com.example.dddsample.domain.exception;

import org.jspecify.annotations.NullMarked;

/**
 * 指定した商品が存在しない場合にスローされる例外。
 */
@NullMarked
public class ProductNotFoundException extends RuntimeException {

    /**
     * @param productId 見つからなかった商品ID
     */
    public ProductNotFoundException(final String productId) {
        super("商品が見つかりません: " + productId);
    }
}
