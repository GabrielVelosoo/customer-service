package io.github.gabrielvelosoo.customerservice.application.usecase;

import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerResponseDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerUpdateDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerCreatedEvent;
import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerDeletedEvent;
import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerUpdatedEvent;
import io.github.gabrielvelosoo.customerservice.application.mapper.CustomerMapper;
import io.github.gabrielvelosoo.customerservice.application.validator.custom.CustomerValidator;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.service.CustomerService;
import io.github.gabrielvelosoo.customerservice.infrastructure.messaging.producer.CustomerProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerUseCaseImplTest {

    @Mock
    CustomerService customerService;

    @Mock
    CustomerMapper customerMapper;

    @Mock
    CustomerValidator customerValidator;

    @Mock
    CustomerProducer customerProducer;

    @InjectMocks
    CustomerUseCaseImpl customerUseCase;

    CustomerRequestDTO customerRequestDTO;
    CustomerResponseDTO customerResponseDTO;
    CustomerUpdateDTO customerUpdateDTO;
    Customer customer;

    @BeforeEach
    void setUp() {
        customerRequestDTO = new CustomerRequestDTO(
                "XXXXX",
                "YYYYY",
                "abcd@example.com",
                "abcd",
                "00000000000",
                "00000000",
                LocalDate.of(1990, 1, 1)
        );

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

        customerResponseDTO = new CustomerResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getCpf(),
                customer.getCep(),
                customer.getBirthDate()
        );

        customerUpdateDTO = new CustomerUpdateDTO(
                "YYYYY",
                "XXXXX",
                "00000000000",
                LocalDate.of(1990, 1, 1)
        );
    }

    @Test
    void shouldCreateCustomerAndPublishEventSuccessfully() {
        when(customerMapper.toEntity(customerRequestDTO)).thenReturn(customer);
        when(customerService.save(customer)).thenReturn(customer);
        when(customerMapper.toDTO(customer)).thenReturn(customerResponseDTO);

        CustomerResponseDTO result = customerUseCase.create(customerRequestDTO);

        verify(customerValidator, times(1)).validateOnCreate(customer);
        verify(customerService, times(1)).save(customer);
        verify(customerMapper, times(1)).toEntity(customerRequestDTO);
        verify(customerMapper, times(1)).toDTO(customer);

        ArgumentCaptor<CustomerCreatedEvent> eventCaptor = ArgumentCaptor.forClass(CustomerCreatedEvent.class);
        verify(customerProducer, times(1)).publishCustomerCreated(eventCaptor.capture());

        CustomerCreatedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(customer.getId(), publishedEvent.customerId());
        assertEquals(customer.getName(), publishedEvent.name());
        assertEquals(customer.getLastName(), publishedEvent.lastName());
        assertEquals(customer.getEmail(), publishedEvent.email());

        assertEquals(customerResponseDTO.id(), result.id());
        assertEquals(customerResponseDTO.name(), result.name());
        assertEquals(customerResponseDTO.lastName(), result.lastName());
        assertEquals(customerResponseDTO.email(), result.email());
        assertEquals(customerResponseDTO.cpf(), result.cpf());
        assertEquals(customerResponseDTO.cep(), result.cep());
    }

    @Test
    void shouldEditCustomerAndPublishEventSuccessfully() {
        when(customerService.findById(customer.getId())).thenReturn(customer);
        Customer editedCustomer = new Customer(
                customer.getId(),
                customerUpdateDTO.name(),
                customerUpdateDTO.lastName(),
                customer.getKeycloakUserId(),
                customer.getEmail(),
                customerUpdateDTO.cpf(),
                customer.getCep(),
                customer.getBirthDate()
        );
        when(customerService.edit(customer)).thenReturn(editedCustomer);

        CustomerResponseDTO updatedDTO = new CustomerResponseDTO(
                editedCustomer.getId(),
                editedCustomer.getName(),
                editedCustomer.getLastName(),
                editedCustomer.getEmail(),
                editedCustomer.getCpf(),
                editedCustomer.getCep(),
                editedCustomer.getBirthDate()
        );
        when(customerMapper.toDTO(editedCustomer)).thenReturn(updatedDTO);

        CustomerResponseDTO result = customerUseCase.edit(customer.getId(), customerUpdateDTO);

        verify(customerService, times(1)).findById(customer.getId());
        verify(customerValidator, times(1)).validateOnUpdate(customer.getId(), customerUpdateDTO);
        verify(customerMapper, times(1)).edit(customer, customerUpdateDTO);
        verify(customerService, times(1)).edit(customer);
        verify(customerMapper, times(1)).toDTO(editedCustomer);

        ArgumentCaptor<CustomerUpdatedEvent> eventCaptor = ArgumentCaptor.forClass(CustomerUpdatedEvent.class);
        verify(customerProducer, times(1)).publishCustomerUpdated(eventCaptor.capture());

        CustomerUpdatedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(customer.getId(), publishedEvent.customerId());
        assertEquals(customerUpdateDTO.name(), publishedEvent.name());
        assertEquals(customerUpdateDTO.lastName(), publishedEvent.lastName());

        assertEquals(updatedDTO.id(), result.id());
        assertEquals(customerUpdateDTO.name(), result.name());
        assertEquals(customerUpdateDTO.lastName(), result.lastName());
        assertEquals(customerUpdateDTO.cpf(), result.cpf());
    }

    @Test
    void shouldDeleteCustomerAndPublishEventSuccessfully() {
        when(customerService.findById(customer.getId())).thenReturn(customer);

        customerUseCase.delete(customer.getId());

        verify(customerService, times(1)).findById(customer.getId());
        verify(customerService, times(1)).delete(customer);

        ArgumentCaptor<CustomerDeletedEvent> eventCaptor = ArgumentCaptor.forClass(CustomerDeletedEvent.class);
        verify(customerProducer, times(1)).publishCustomerDeleted(eventCaptor.capture());

        CustomerDeletedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(customer.getId(), publishedEvent.customerId());
    }
}
