package io.github.gabrielvelosoo.customerservice.domain.service;

import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.repository.CustomerRepository;
import io.github.gabrielvelosoo.customerservice.infrastructure.exception.RecordNotFoundException;
import org.junit.jupiter.api.BeforeEach;
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
                10L,
                "XXXXX",
                "YYYYY",
                UUID.randomUUID().toString(),
                "abcd@example.com",
                "00000000000",
                "00000000",
                LocalDate.of(1990, 2, 23)
        );
    }

    @Test
    void shouldCreateCustomerSuccessfully() {
        when(customerRepository.save(customer)).thenReturn(customer);

        Customer savedCustomer = customerService.save(customer);

        assertNotNull(savedCustomer);
        assertEquals(10L, savedCustomer.getId());
        assertEquals("XXXXX", savedCustomer.getName());

        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void shouldFindCustomerByIdSuccessfully() {
        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));

        Customer found = customerService.findById(10L);

        assertNotNull(found);
        assertEquals(10L, found.getId());

        verify(customerRepository, times(1)).findById(10L);
    }

    @Test
    void shouldThrowExceptionWhenCustomerDoesNotExist() {
        when(customerRepository.findById(10L)).thenReturn(Optional.empty());

        RecordNotFoundException e = assertThrows(
                RecordNotFoundException.class,
                () -> customerService.findById(10L)
        );

        assertEquals("Customer not found: 10", e.getMessage());

        verify(customerRepository, times(1)).findById(10L);
    }

    @Test
    void shouldSaveEditedCustomerSuccessfully() {
        when(customerRepository.save(customer)).thenReturn(customer);

        Customer editedCustomer = customerService.edit(customer);

        assertNotNull(editedCustomer);
        assertEquals("XXXXX", editedCustomer.getName());

        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void shouldDeleteCustomerUsingRepositorySuccessfully() {
        doNothing().when(customerRepository).delete(customer);
        customerService.delete(customer);
        verify(customerRepository, times(1)).delete(customer);
    }
}
