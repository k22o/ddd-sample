package com.example.dddsample.infrastructure.db.repository;

import com.example.dddsample.domain.exception.ProductNotFoundException;
import com.example.dddsample.domain.model.product.Product;
import com.example.dddsample.domain.model.product.ProductId;
import com.example.dddsample.infrastructure.db.mapper.ProductMapper;
import com.example.dddsample.infrastructure.db.record.ProductRecord;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link ProductRepositoryImpl} の単体テスト。{@link ProductMapper} はモック化する。
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"NonAsciiCharacters"})
class ProductRepositoryImplTest {

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductRepositoryImpl productRepository;

    private ProductRecord productRecord(final String id, final String name) {
        return new ProductRecord(id, name, new BigDecimal("1000.00"), "JPY", "説明");
    }

    @Nested
    class FindById {

        @Test
        void 登録済みの商品IDを指定すると商品を返す() {
            when(productMapper.findById("product-1")).thenReturn(productRecord("product-1", "商品A"));

            final Product product = productRepository.findById(new ProductId("product-1"));

            assertThat(product.id()).isEqualTo(new ProductId("product-1"));
            assertThat(product.name()).isEqualTo("商品A");
        }

        @Test
        void 未登録の商品IDを指定するとProductNotFoundExceptionをスローする() {
            when(productMapper.findById("unknown")).thenReturn(null);

            assertThatThrownBy(() -> productRepository.findById(new ProductId("unknown")))
                    .isInstanceOf(ProductNotFoundException.class);
        }
    }

    @Nested
    class FindByIds {

        @Test
        void 複数の商品IDを指定すると該当する商品一覧を返す() {
            when(productMapper.findByIds(List.of("product-1", "product-2")))
                    .thenReturn(List.of(productRecord("product-1", "商品A"), productRecord("product-2", "商品B")));

            final List<Product> products = productRepository.findByIds(
                    List.of(new ProductId("product-1"), new ProductId("product-2")));

            assertThat(products).extracting(Product::name).containsExactlyInAnyOrder("商品A", "商品B");
        }

        @Test
        void 空リストを指定するとマッパーを呼び出さずに空リストを返す() {
            final List<Product> products = productRepository.findByIds(List.of());

            assertThat(products).isEmpty();
            verify(productMapper, never()).findByIds(any());
        }
    }
}
