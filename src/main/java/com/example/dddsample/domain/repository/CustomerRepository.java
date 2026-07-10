package com.example.dddsample.domain.repository;

import com.example.dddsample.domain.model.customer.Customer;
import com.example.dddsample.domain.model.customer.CustomerId;
import org.jspecify.annotations.NullMarked;

/**
 * {@link Customer} 集約の永続化を担うリポジトリ。
 */
@NullMarked
public interface CustomerRepository {

    /**
     * 顧客IDで顧客を取得する。
     *
     * @param id 顧客ID
     * @return 顧客
     * @throws com.example.dddsample.domain.exception.CustomerNotFoundException 顧客が存在しない場合
     */
    Customer findById(CustomerId id);
}
