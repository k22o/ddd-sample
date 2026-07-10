package com.example.dddsample.infrastructure.db.record;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * {@code orders} テーブルのSELECT結果を受け取るPOJO。
 *
 * @param id             注文ID
 * @param customerId     顧客ID
 * @param status         注文ステータス（DB上の文字列表現）
 * @param totalAmount    合計金額
 * @param totalCurrency  合計金額の通貨コード
 * @param postalCode     配送先郵便番号
 * @param prefecture     配送先都道府県
 * @param city           配送先市区町村
 * @param street         配送先番地・建物名
 * @param paymentId      外部決済サービスの支払いID
 * @param createdAt      注文日時
 */
@NullMarked
public record OrderRecord(
        String id,                     // 注文ID
        String customerId,             // 顧客ID
        String status,                 // 注文ステータス（DB上の文字列表現）
        BigDecimal totalAmount,        // 合計金額
        String totalCurrency,          // 合計金額の通貨コード
        String postalCode,             // 配送先郵便番号
        String prefecture,             // 配送先都道府県
        String city,                   // 配送先市区町村
        String street,                 // 配送先番地・建物名
        @Nullable String paymentId,    // 外部決済サービスの支払いID（未確定の場合は null）
        LocalDateTime createdAt) {     // 注文日時
}
