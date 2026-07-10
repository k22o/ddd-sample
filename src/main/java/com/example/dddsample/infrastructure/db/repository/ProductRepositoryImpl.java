package com.example.dddsample.infrastructure.db.repository;

import com.example.dddsample.domain.exception.ProductNotFoundException;
import com.example.dddsample.domain.model.product.Product;
import com.example.dddsample.domain.model.product.ProductId;
import com.example.dddsample.domain.model.shared.Money;
import com.example.dddsample.domain.repository.ProductRepository;
import com.example.dddsample.infrastructure.db.mapper.ProductMapper;
import com.example.dddsample.infrastructure.db.record.ProductRecord;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * {@link ProductRepository} の実装クラス。
 */
@Repository
@RequiredArgsConstructor
@NullMarked
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductMapper productMapper;

    /**
     * {@inheritDoc}
     *
     * @throws ProductNotFoundException 商品が存在しない場合
     */
    @Override
    public Product findById(final ProductId id) {
        final ProductRecord record = productMapper.findById(id.value());
        if (record == null) {
            throw new ProductNotFoundException(id.value());
        }
        return toDomain(record);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> findByIds(final List<ProductId> ids) {
        final List<String> rawIds = ids.stream().map(ProductId::value).toList();
        return productMapper.findByIds(rawIds).stream().map(this::toDomain).toList();
    }

    /**
     * レコードをドメインモデルに変換する。
     *
     * @param record 変換対象のレコード
     * @return 変換後の {@link Product}
     */
    private Product toDomain(final ProductRecord record) {
        return new Product(
                new ProductId(record.id()),
                record.name(),
                new Money(record.priceAmount(), record.priceCurrency()),
                record.description());
    }
}
