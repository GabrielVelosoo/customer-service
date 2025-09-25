package io.github.gabrielvelosoo.customerservice.infrastructure.messaging.consumer;

import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerCreatedEvent;
import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerDeletedEvent;
import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerUpdatedEvent;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.service.auth.IdentityProvider;
import io.github.gabrielvelosoo.customerservice.domain.service.customer.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeycloakConsumerTest {

    @Mock
    IdentityProvider identityProvider;

    @Mock
    CustomerService customerService;

    @InjectMocks
    KeycloakConsumer keycloakConsumer;

    Customer customer;
    CustomerCreatedEvent customerCreatedEvent;
    CustomerUpdatedEvent customerUpdatedEvent;
    CustomerDeletedEvent customerDeletedEvent;

    @BeforeEach
    void setUp() {
        customer = new Customer(
                1L,
                "Gabriel",
                "Veloso",
                "abcd@example.com",
                "85656417085",
                "76964604",
                LocalDate.of(2004, 4, 10)
        );

        customerCreatedEvent = new CustomerCreatedEvent(
                1L,
                "test@test.com",
                "John",
                "Doe"
        );

        customerUpdatedEvent = new CustomerUpdatedEvent(
                1L,
                "John",
                "Doe"
        );

        customerDeletedEvent = new CustomerDeletedEvent(
                1L
        );
    }

    @Test
    void shouldCreateKeycloakUserWhenCustomerHasNoUserId() {
        when(customerService.findById(1L)).thenReturn(customer);
        when(identityProvider.createUser(customerCreatedEvent.email(), customerCreatedEvent.name(), customerCreatedEvent.lastName())).thenReturn("kc-uuid-123");
        keycloakConsumer.handleCustomerCreated(customerCreatedEvent);
        assertEquals("kc-uuid-123", customer.getKeycloakUserId());
        verify(customerService).save(customer);
        verify(identityProvider).createUser(customerCreatedEvent.email(), customerCreatedEvent.name(), customerCreatedEvent.lastName());
    }

    @Test
    void shouldNotCreateKeycloakUserIfAlreadyExists() {
        customer.setKeycloakUserId("kc-uuid-999");
        when(customerService.findById(1L)).thenReturn(customer);
        keycloakConsumer.handleCustomerCreated(customerCreatedEvent);
        verify(identityProvider, never()).createUser(customerCreatedEvent.email(), customerCreatedEvent.name(), customerCreatedEvent.lastName());
        verify(customerService, never()).save(customer);
    }

    @Test
    void shouldUpdateKeycloakUserIfUserIdExists() {
        customer.setKeycloakUserId("kc-uuid-123");
        when(customerService.findById(1L)).thenReturn(customer);
        keycloakConsumer.handleCustomerUpdated(customerUpdatedEvent);
        verify(identityProvider).editUser("kc-uuid-123", customerUpdatedEvent.name(), customerUpdatedEvent.lastName());
    }

    @Test
    void shouldNotUpdateKeycloakUserIfUserIdIsNull() {
        when(customerService.findById(1L)).thenReturn(customer);
        keycloakConsumer.handleCustomerUpdated(customerUpdatedEvent);
        verify(identityProvider, never()).editUser(anyString(), anyString(), anyString());
    }

    @Test
    void shouldDeleteKeycloakUserIfUserIdExists() {
        customer.setKeycloakUserId("kc-uuid-123");
        when(customerService.findById(1L)).thenReturn(customer);
        keycloakConsumer.handleCustomerDeleted(customerDeletedEvent);
        verify(identityProvider).deleteUser("kc-uuid-123");
    }

    @Test
    void shouldNotDeleteKeycloakUserIfUserIdIsNull() {
        when(customerService.findById(1L)).thenReturn(customer);
        keycloakConsumer.handleCustomerDeleted(customerDeletedEvent);
        verify(identityProvider, never()).deleteUser(anyString());
    }
}