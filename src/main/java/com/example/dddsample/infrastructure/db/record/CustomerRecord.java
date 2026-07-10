package com.example.dddsample.infrastructure.db.record;

import org.jspecify.annotations.NullMarked;

/**
 * {@code customers} テーブルのSELECT結果を受け取るPOJO。
 *
 * @param id         顧客ID
 * @param name       氏名
 * @param email      メールアドレス
 * @param postalCode 郵便番号
 * @param prefecture 都道府県
 * @param city       市区町村
 * @param street     番地・建物名
 */
@NullMarked
public record CustomerRecord(
        String id,             // 顧客ID
        String name,           // 氏名
        String email,          // メールアドレス
        String postalCode,     // 郵便番号
        String prefecture,     // 都道府県
        String city,           // 市区町村
        String street) {       // 番地・建物名
}
