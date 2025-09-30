package io.github.gabrielvelosoo.customerservice.application.validator.custom;

import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerUpdateDTO;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.repository.CustomerRepository;
import io.github.gabrielvelosoo.customerservice.infrastructure.exception.DuplicateRecordException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerValidatorTest {

    @Mock
    CustomerRepository customerRepository;

    @InjectMocks
    CustomerValidator customerValidator;

    Customer customer;
    CustomerUpdateDTO customerUpdateDTO;

    @BeforeEach
    void setUp() {
        customer = new Customer(
                1L,
                "unit",
                "test",
                UUID.randomUUID().toString(),
                "test@example.com",
                "00000000000",
                "00000000",
                LocalDate.of(1990, 2, 23)
        );

        customerUpdateDTO = new CustomerUpdateDTO(
                "test",
                "unit",
                "00000000000",
                LocalDate.of(1990, 1, 1)
        );
    }

    @Nested
    class ValidateOnCreateTests {

        @Test
        void shouldThrowExceptionWhenEmailAlreadyExistsOnCreate() {
            when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));

            DuplicateRecordException e = assertThrows(
                    DuplicateRecordException.class,
                    () -> customerValidator.validateOnCreate(customer)
            );

            assertEquals("There is already an account registered with this email", e.getMessage());

            verify(customerRepository, times(1)).findByEmail(customer.getEmail());
            verify(customerRepository, never()).findByCpf(anyString());
        }

        @Test
        void shouldThrowExceptionWhenCpfAlreadyExistsOnCreate() {
            when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.empty());
            when(customerRepository.findByCpf(customer.getCpf())).thenReturn(Optional.of(customer));

            DuplicateRecordException e = assertThrows(
                    DuplicateRecordException.class,
                    () -> customerValidator.validateOnCreate(customer)
            );

            assertEquals("There is already an account registered with this CPF", e.getMessage());

            verify(customerRepository, times(1)).findByEmail(customer.getEmail());
            verify(customerRepository, times(1)).findByCpf(customer.getCpf());
        }

        @Test
        void shouldPassValidationOnCreateWhenEmailAndCpfAreUnique() {
            when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.empty());
            when(customerRepository.findByCpf(customer.getCpf())).thenReturn(Optional.empty());

            customerValidator.validateOnCreate(customer);

            verify(customerRepository, times(1)).findByEmail(customer.getEmail());
            verify(customerRepository, times(1)).findByCpf(customer.getCpf());
        }


    }

    @Nested
    class ValidateOnUpdateTests {

        @Test
        void shouldThrowExceptionWhenCpfAlreadyExistsOnUpdate() {
            when(customerRepository.findByCpfAndNotId(customerUpdateDTO.cpf(), customer.getId())).thenReturn(Optional.of(customer));

            DuplicateRecordException e = assertThrows(
                    DuplicateRecordException.class,
                    () -> customerValidator.validateOnUpdate(customer.getId(), customerUpdateDTO)
            );

            assertEquals("There is already an account registered with this CPF", e.getMessage());

            verify(customerRepository, times(1)).findByCpfAndNotId(customerUpdateDTO.cpf(), customer.getId());
        }

        @Test
        void shouldPassValidationOnUpdateWhenCpfIsUnique() {
            when(customerRepository.findByCpfAndNotId(customerUpdateDTO.cpf(), customer.getId())).thenReturn(Optional.empty());

            customerValidator.validateOnUpdate(customer.getId(), customerUpdateDTO);

            verify(customerRepository, times(1)).findByCpfAndNotId(customerUpdateDTO.cpf(), customer.getId());
        }
    }
}