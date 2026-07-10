package com.example.dddsample.infrastructure.db.mapper;

import com.example.dddsample.infrastructure.db.record.ProductRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * {@code products} テーブルに対するMyBatisマッパー。SQLは {@code mapper/ProductMapper.xml} に定義する。
 */
@Mapper
@NullMarked
public interface ProductMapper {

    /**
     * 商品IDでレコードを取得する。
     *
     * @param id 商品ID
     * @return レコード。存在しない場合は {@code null}
     */
    @Nullable
    ProductRecord findById(@Param("id") String id);

    /**
     * 複数の商品IDに紐づくレコード一覧を取得する。
     *
     * @param ids 商品IDのリスト
     * @return レコード一覧
     */
    List<ProductRecord> findByIds(@Param("ids") List<String> ids);
}
