package io.github.gabrielvelosoo.customerservice.application.validator.custom;

import io.github.gabrielvelosoo.customerservice.domain.entity.Address;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.repository.CustomerRepository;
import io.github.gabrielvelosoo.customerservice.domain.service.auth.AuthService;
import io.github.gabrielvelosoo.customerservice.infrastructure.exception.BusinessRuleException;
import io.github.gabrielvelosoo.customerservice.infrastructure.exception.RecordNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressValidatorTest {

    @Mock
    CustomerRepository customerRepository;

    @Mock
    AuthService authService;

    @InjectMocks
    AddressValidator addressValidator;

    Customer customer1;
    Customer customer2;
    Address address;

    @BeforeEach
    void setUp() {
        customer1 = new Customer(
                1L,
                "test1",
                "unit",
                UUID.randomUUID().toString(),
                "abcd@example.com",
                "85656417085",
                "76964604",
                LocalDate.of(2004, 4, 10)
        );

        customer2 = new Customer(
                2L,
                "test2",
                "unit",
                UUID.randomUUID().toString(),
                "abcd@example.com",
                "61980992029",
                "72505414",
                LocalDate.of(2007, 5, 22)
        );

        address = new Address(
                1L,
                "old",
                "test",
                "21563547665",
                "Quadra QR 205 Área Especial 2",
                "20",
                "Santa Maria",
                "Brasília",
                "Distrito Federal",
                "72505414",
                "",
                customer1
        );
    }

    @Nested
    class ValidateOnCreateTests {

        @Test
        void shouldThrowExceptionValidationOnCreateWhenCustomerDoesNotExist() {
            when(customerRepository.existsById(1L)).thenReturn(false);

            RecordNotFoundException e = assertThrows(
                    RecordNotFoundException.class,
                    () -> addressValidator.validateOnCreate(address)
            );

            assertEquals("Customer not found: 1", e.getMessage());
            verify(customerRepository, times(1)).existsById(1L);
        }

        @Test
        void shouldPassValidationOnCreateWhenCustomerExists() {
            when(customerRepository.existsById(1L)).thenReturn(true);

            addressValidator.validateOnCreate(address);

            verify(customerRepository, times(1)).existsById(1L);
        }
    }

    @Nested
    class ValidateOnUpdateAndDeleteTests {

        @Test
        void shouldThrowExceptionValidationOnUpdateAndDeleteWhenCustomerDoesNotExist() {
            when(customerRepository.existsById(1L)).thenReturn(false);

            RecordNotFoundException e = assertThrows(
                    RecordNotFoundException.class,
                    () -> addressValidator.validateOnUpdateAndDelete(address)
            );

            assertEquals("Customer not found: 1", e.getMessage());
            verify(customerRepository, times(1)).existsById(1L);
        }

        @Test
        void shouldThrowExceptionValidationOnUpdateAndDeleteWhenAddressNotBelongsToLoggedCustomer() {
            when(customerRepository.existsById(1L)).thenReturn(true);
            when(authService.getLoggedCustomer()).thenReturn(customer2);

            BusinessRuleException e = assertThrows(
                    BusinessRuleException.class,
                    () -> addressValidator.validateOnUpdateAndDelete(address)
            );

            assertEquals("You don´t have permission to manage this address", e.getMessage());
            verify(customerRepository, times(1)).existsById(1L);
            verify(authService, times(1)).getLoggedCustomer();
        }

        @Test
        void shouldPassValidationOnUpdateAndDeleteWhenAddressBelongsToLoggedCustomer() {
            when(customerRepository.existsById(1L)).thenReturn(true);
            when(authService.getLoggedCustomer()).thenReturn(customer1);

            addressValidator.validateOnUpdateAndDelete(address);

            verify(customerRepository, times(1)).existsById(1L);
            verify(authService, times(1)).getLoggedCustomer();
        }
    }
}