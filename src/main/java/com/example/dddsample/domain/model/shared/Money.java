package com.example.dddsample.domain.model.shared;

import org.jspecify.annotations.NullMarked;

import java.math.BigDecimal;

/**
 * 金額を表す値オブジェクト。負値は不可。
 *
 * @param amount   金額
 * @param currency 通貨コード（例: "JPY"）
 */
@NullMarked
public record Money(BigDecimal amount, String currency) {

    /**
     * @throws IllegalArgumentException 金額が負値、または通貨コードが空の場合
     */
    public Money {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("金額は0以上である必要があります");
        }
        if (currency.isBlank()) {
            throw new IllegalArgumentException("通貨コードは必須です");
        }
    }

    /**
     * 金額と通貨コードを指定してインスタンスを生成する。
     *
     * @param amount   金額
     * @param currency 通貨コード
     * @return {@link Money} インスタンス
     */
    public static Money of(final BigDecimal amount, final String currency) {
        return new Money(amount, currency);
    }

    /**
     * 同一通貨の金額を加算する。
     *
     * @param other 加算対象の金額
     * @return 加算結果
     * @throws IllegalArgumentException 通貨コードが異なる場合
     */
    public Money add(final Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("異なる通貨同士は加算できません");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    /**
     * 金額に数量を乗算する。
     *
     * @param quantity 乗数
     * @return 乗算結果
     */
    public Money multiply(final int quantity) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(quantity)), this.currency);
    }
}
