package com.example.dddsample.domain.client;

import com.example.dddsample.domain.model.customer.CustomerId;
import com.example.dddsample.domain.model.shared.Money;
import org.jspecify.annotations.NullMarked;

/**
 * 外部決済サービスとのインターフェース。
 */
@NullMarked
public interface PaymentClient {

    /**
     * 決済を実行する。
     *
     * @param customerId 顧客ID
     * @param amount     決済金額
     * @return 外部決済サービスが払い出した決済ID
     */
    String charge(CustomerId customerId, Money amount);

    /**
     * 決済をキャンセルする。
     *
     * @param paymentId キャンセル対象の決済ID
     */
    void cancel(String paymentId);
}
