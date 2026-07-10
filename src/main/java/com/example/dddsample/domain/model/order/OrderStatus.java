package com.example.dddsample.domain.model.order;

import org.jspecify.annotations.NullMarked;

/**
 * 注文のステータスを表す列挙型。
 */
@NullMarked
public enum OrderStatus {

    /** 作成済み・未確定。 */
    PENDING,

    /** 確定済み（在庫引当・決済完了）。 */
    CONFIRMED,

    /** キャンセル済み。 */
    CANCELLED
}
