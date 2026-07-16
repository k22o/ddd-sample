package com.example.dddsample.infrastructure.db.mapper;

import com.example.dddsample.infrastructure.db.record.OrderItemRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * {@code order_items} テーブルに対するMyBatisマッパー。SQLは {@code mapper/OrderItemMapper.xml} に定義する。
 */
@Mapper
@NullMarked
public interface OrderItemMapper {

    /**
     * 注文IDに紐づく明細レコード一覧を取得する。
     *
     * @param orderId 注文ID
     * @return レコード一覧
     */
    List<OrderItemRecord> findByOrderId(@Param("orderId") String orderId);

    /**
     * 複数の注文IDに紐づく明細レコード一覧を一括取得する。
     *
     * @param orderIds 注文IDのリスト
     * @return レコード一覧
     */
    List<OrderItemRecord> findByOrderIds(@Param("orderIds") List<String> orderIds);

    /**
     * 注文明細レコードを一括登録する。
     *
     * @param records 登録対象のレコード一覧
     * @return 登録件数
     */
    int insertAll(@Param("records") List<OrderItemRecord> records);
}
