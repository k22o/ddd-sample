package com.example.dddsample.domain.model.customer;

import com.example.dddsample.domain.model.shared.Address;
import org.jspecify.annotations.NullMarked;

/**
 * 顧客を表す集約。
 *
 * @param id      顧客ID
 * @param name    氏名
 * @param email   メールアドレス
 * @param address 住所
 */
@NullMarked
public record Customer(CustomerId id, String name, String email, Address address) {

    /**
     * @throws IllegalArgumentException 氏名またはメールアドレスが空の場合
     */
    public Customer {
        if (name.isBlank()) {
            throw new IllegalArgumentException("氏名は必須です");
        }
        if (email.isBlank()) {
            throw new IllegalArgumentException("メールアドレスは必須です");
        }
    }
}
