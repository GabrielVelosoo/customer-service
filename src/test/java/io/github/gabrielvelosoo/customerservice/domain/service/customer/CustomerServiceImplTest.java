package io.github.gabrielvelosoo.customerservice.domain.service.customer;

import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.repository.CustomerRepository;
import io.github.gabrielvelosoo.customerservice.infrastructure.exception.RecordNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    CustomerRepository customerRepository;

    @InjectMocks
    CustomerServiceImpl customerService;

    Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer(
                1L,
                "test1",
                "unit",
                UUID.randomUUID().toString(),
                "test1@example.com",
                "00000000000",
                "00000000",
                LocalDate.of(1990, 2, 23)
        );
    }

    @Nested
    class CreateTests {

        @Test
        void shouldCreateCustomerSuccessfully() {
            when(customerRepository.save(customer)).thenReturn(customer);

            Customer savedCustomer = customerService.save(customer);

            assertNotNull(savedCustomer);
            assertEquals(1L, savedCustomer.getId());
            assertEquals("test1", savedCustomer.getName());

            verify(customerRepository, times(1)).save(customer);
        }
    }

    @Nested
    class FindByIdTests {

        @Test
        void shouldFindCustomerByIdSuccessfully() {
            when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

            Customer foundCustomer = customerService.findById(1L);

            assertNotNull(foundCustomer);
            assertEquals(1L, foundCustomer.getId());

            verify(customerRepository, times(1)).findById(1L);
        }

        @Test
        void shouldThrowExceptionWhenCustomerDoesNotExist() {
            when(customerRepository.findById(1L)).thenReturn(Optional.empty());

            RecordNotFoundException e = assertThrows(
                    RecordNotFoundException.class,
                    () -> customerService.findById(1L)
            );

            assertEquals("Customer not found: 1", e.getMessage());

            verify(customerRepository, times(1)).findById(1L);
        }
    }

    @Nested
    class EditTests {

        @Test
        void shouldSaveEditedCustomerSuccessfully() {
            when(customerRepository.save(customer)).thenReturn(customer);

            Customer editedCustomer = customerService.edit(customer);

            assertNotNull(editedCustomer);
            assertEquals("test1", editedCustomer.getName());

            verify(customerRepository, times(1)).save(customer);
        }
    }

    @Nested
    class DeleteTests {

        @Test
        void shouldDeleteCustomerUsingRepositorySuccessfully() {
            doNothing().when(customerRepository).delete(customer);
            customerService.delete(customer);
            verify(customerRepository, times(1)).delete(customer);
        }
    }
}
