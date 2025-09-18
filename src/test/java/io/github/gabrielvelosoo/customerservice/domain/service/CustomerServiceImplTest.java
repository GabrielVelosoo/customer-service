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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;

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
    void shouldSaveCustomer() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        Customer savedCustomer = customerService.save(customer);
        assertNotNull(savedCustomer);
        assertEquals(10L, savedCustomer.getId());
        assertEquals("XXXXX", savedCustomer.getName());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void shouldFindCustomerById() {
        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));
        Customer found = customerService.findById(10L);
        assertNotNull(found);
        assertEquals(10L, found.getId());
        verify(customerRepository).findById(10L);
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        when(customerRepository.findById(10L)).thenReturn(Optional.empty());
        RecordNotFoundException e = assertThrows(
                RecordNotFoundException.class,
                () -> customerService.findById(10L)
        );
        assertEquals("Customer not found: 10", e.getMessage());
        verify(customerRepository).findById(10L);
    }

    @Test
    void shouldEditCustomer() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        Customer editedCustomer = customerService.edit(customer);
        assertNotNull(editedCustomer);
        assertEquals("XXXXX", editedCustomer.getName());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void shouldDeleteCustomer() {
        doNothing().when(customerRepository).delete(any(Customer.class));
        customerService.delete(customer);
        verify(customerRepository).delete(any(Customer.class));
    }
}
