package com.example.dddsample.infrastructure.db.mapper;

import com.example.dddsample.infrastructure.db.record.OrderRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * {@code orders} テーブルに対するMyBatisマッパー。SQLは {@code mapper/OrderMapper.xml} に定義する。
 */
@Mapper
@NullMarked
public interface OrderMapper {

    /**
     * 注文IDでレコードを取得する。
     *
     * @param id 注文ID
     * @return レコード。存在しない場合は {@code null}
     */
    @Nullable
    OrderRecord findById(@Param("id") String id);

    /**
     * 顧客IDに紐づくレコード一覧を取得する。
     *
     * @param customerId 顧客ID
     * @return レコード一覧
     */
    List<OrderRecord> findByCustomerId(@Param("customerId") String customerId);

    /**
     * 注文レコードを新規登録する。
     *
     * @param record 登録対象のレコード
     * @return 登録件数
     */
    int insert(OrderRecord record);

    /**
     * 注文のステータスと支払いIDを更新する。
     *
     * @param id        注文ID
     * @param status    更新後のステータス
     * @param paymentId 更新後の支払いID
     * @return 更新件数
     */
    int updateStatusAndPayment(
            @Param("id") String id,
            @Param("status") String status,
            @Param("paymentId") @Nullable String paymentId);
}
