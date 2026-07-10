package com.example.dddsample.infrastructure.db.repository;

import com.example.dddsample.domain.exception.CustomerNotFoundException;
import com.example.dddsample.domain.model.customer.Customer;
import com.example.dddsample.domain.model.customer.CustomerId;
import com.example.dddsample.domain.model.shared.Address;
import com.example.dddsample.domain.repository.CustomerRepository;
import com.example.dddsample.infrastructure.db.mapper.CustomerMapper;
import com.example.dddsample.infrastructure.db.record.CustomerRecord;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Repository;

/**
 * {@link CustomerRepository} の実装クラス。
 */
@Repository
@RequiredArgsConstructor
@NullMarked
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerMapper customerMapper;

    /**
     * {@inheritDoc}
     *
     * @throws CustomerNotFoundException 顧客が存在しない場合
     */
    @Override
    public Customer findById(final CustomerId id) {
        final CustomerRecord record = customerMapper.findById(id.value());
        if (record == null) {
            throw new CustomerNotFoundException(id.value());
        }
        return toDomain(record);
    }

    /**
     * レコードをドメインモデルに変換する。
     *
     * @param record 変換対象のレコード
     * @return 変換後の {@link Customer}
     */
    private Customer toDomain(final CustomerRecord record) {
        return new Customer(
                new CustomerId(record.id()),
                record.name(),
                record.email(),
                new Address(record.postalCode(), record.prefecture(), record.city(), record.street()));
    }
}
