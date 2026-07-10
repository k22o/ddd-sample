package com.example.dddsample.infrastructure.db.mapper;

import com.example.dddsample.infrastructure.db.record.CustomerRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * {@code customers} テーブルに対するMyBatisマッパー。SQLは {@code mapper/CustomerMapper.xml} に定義する。
 */
@Mapper
@NullMarked
public interface CustomerMapper {

    /**
     * 顧客IDでレコードを取得する。
     *
     * @param id 顧客ID
     * @return レコード。存在しない場合は {@code null}
     */
    @Nullable
    CustomerRecord findById(@Param("id") String id);
}
