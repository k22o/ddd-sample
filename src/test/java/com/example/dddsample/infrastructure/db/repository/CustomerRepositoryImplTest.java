package com.example.dddsample.infrastructure.db.repository;

import com.example.dddsample.domain.exception.CustomerNotFoundException;
import com.example.dddsample.domain.model.customer.Customer;
import com.example.dddsample.domain.model.customer.CustomerId;
import com.example.dddsample.infrastructure.db.mapper.CustomerMapper;
import com.example.dddsample.infrastructure.db.record.CustomerRecord;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * {@link CustomerRepositoryImpl} の単体テスト。{@link CustomerMapper} はモック化する。
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"NonAsciiCharacters"})
class CustomerRepositoryImplTest {

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerRepositoryImpl customerRepository;

    private CustomerRecord customerRecord(final String id) {
        return new CustomerRecord(id, "山田太郎", "yamada@example.com", "100-0001", "東京都", "千代田区", "1-1-1");
    }

    @Nested
    class FindById {

        @Test
        void 登録済みの顧客IDを指定すると顧客を返す() {
            when(customerMapper.findById("customer-1")).thenReturn(customerRecord("customer-1"));

            final Customer customer = customerRepository.findById(new CustomerId("customer-1"));

            assertThat(customer.id()).isEqualTo(new CustomerId("customer-1"));
            assertThat(customer.name()).isEqualTo("山田太郎");
            assertThat(customer.address().prefecture()).isEqualTo("東京都");
        }

        @Test
        void 未登録の顧客IDを指定するとCustomerNotFoundExceptionをスローする() {
            when(customerMapper.findById("unknown")).thenReturn(null);

            assertThatThrownBy(() -> customerRepository.findById(new CustomerId("unknown")))
                    .isInstanceOf(CustomerNotFoundException.class);
        }
    }
}
