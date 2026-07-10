package com.example.dddsample.domain.model.shared;

import org.jspecify.annotations.NullMarked;

/**
 * 住所を表す値オブジェクト。
 *
 * @param postalCode 郵便番号
 * @param prefecture 都道府県
 * @param city       市区町村
 * @param street     番地・建物名
 */
@NullMarked
public record Address(String postalCode, String prefecture, String city, String street) {

    /**
     * @throws IllegalArgumentException いずれかの項目が空の場合
     */
    public Address {
        if (postalCode.isBlank()) {
            throw new IllegalArgumentException("郵便番号は必須です");
        }
        if (prefecture.isBlank()) {
            throw new IllegalArgumentException("都道府県は必須です");
        }
        if (city.isBlank()) {
            throw new IllegalArgumentException("市区町村は必須です");
        }
        if (street.isBlank()) {
            throw new IllegalArgumentException("番地・建物名は必須です");
        }
    }
}
