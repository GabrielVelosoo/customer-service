package io.github.gabrielvelosoo.customerservice.domain.repository;

import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerRepositoryTest {

    @Mock
    CustomerRepository customerRepository;

    Customer customer1;
    Customer customer2;

    @BeforeEach
    void setUp() {
        customer1 = new Customer(
                1L,
                "YYYYY",
                "XXXXX",
                "uuid-1",
                "abcd@example.com",
                "00000000000",
                "77777777",
                LocalDate.of(1990, 2, 23)
        );

        customer2 = new Customer(
                2L,
                "XXXXX",
                "YYYYY",
                "uuid-2",
                "dcba@example.com",
                "11111111111",
                "55555555",
                LocalDate.of(2000, 5, 15)
        );
    }

    @Test
    void shouldReturnCustomerWhenCpfExistsAndIdIsDifferent() {
        when(customerRepository.findByCpfAndNotId(customer2.getCpf(), customer1.getId())).thenReturn(Optional.of(customer2));

        Optional<Customer> result = customerRepository.findByCpfAndNotId(customer2.getCpf(), customer1.getId());

        assertTrue(result.isPresent());
        assertEquals(customer2.getId(), result.get().getId());

        verify(customerRepository, times(1)).findByCpfAndNotId(customer2.getCpf(), customer1.getId());
    }

    @Test
    void shouldReturnEmptyWhenCpfDoesNotExistForOtherIds() {
        when(customerRepository.findByCpfAndNotId(customer1.getCpf(), customer1.getId())).thenReturn(Optional.empty());

        Optional<Customer> result = customerRepository.findByCpfAndNotId(customer1.getCpf(), customer1.getId());

        assertTrue(result.isEmpty());
        verify(customerRepository, times(1)).findByCpfAndNotId(customer1.getCpf(), customer1.getId());
    }
}