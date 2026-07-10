package com.example.dddsample.domain.repository;

import com.example.dddsample.domain.model.customer.CustomerId;
import com.example.dddsample.domain.model.order.Order;
import com.example.dddsample.domain.model.order.OrderId;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * {@link Order} 集約の永続化を担うリポジトリ。
 */
@NullMarked
public interface OrderRepository {

    /**
     * 注文IDで注文を取得する。
     *
     * @param id 注文ID
     * @return 注文
     * @throws com.example.dddsample.domain.exception.OrderNotFoundException 注文が存在しない場合
     */
    Order findById(OrderId id);

    /**
     * 顧客IDに紐づく注文一覧を取得する。
     *
     * @param customerId 顧客ID
     * @return 注文一覧（該当なしの場合は空リスト）
     */
    List<Order> findByCustomerId(CustomerId customerId);

    /**
     * 注文を保存する。既存の注文IDであれば更新、存在しなければ新規作成する。
     *
     * @param order 保存対象の注文
     */
    void save(Order order);
}
