package com.example.dddsample.domain.repository;

import com.example.dddsample.domain.model.product.Product;
import com.example.dddsample.domain.model.product.ProductId;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * {@link Product} 集約の永続化を担うリポジトリ。
 */
@NullMarked
public interface ProductRepository {

    /**
     * 商品IDで商品を取得する。
     *
     * @param id 商品ID
     * @return 商品
     * @throws com.example.dddsample.domain.exception.ProductNotFoundException 商品が存在しない場合
     */
    Product findById(ProductId id);

    /**
     * 複数の商品IDに紐づく商品一覧を取得する。
     *
     * @param ids 商品IDのリスト
     * @return 商品一覧（該当なしの場合は空リスト）
     */
    List<Product> findByIds(List<ProductId> ids);
}
