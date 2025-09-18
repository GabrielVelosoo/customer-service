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
public class CustomerUseCaseImplTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private CustomerValidator customerValidator;

    @Mock
    private CustomerProducer customerProducer;

    @InjectMocks
    private CustomerUseCaseImpl customerUseCase;

    private CustomerRequestDTO customerRequestDTO;
    private CustomerResponseDTO customerResponseDTO;
    private Customer customer;

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
    }

    @Test
    void shouldCreateCustomerSuccessfully() {
        when(customerMapper.toEntity(customerRequestDTO)).thenReturn(customer);
        when(customerService.save(customer)).thenReturn(customer);
        when(customerMapper.toDTO(customer)).thenReturn(customerResponseDTO);

        CustomerResponseDTO result = customerUseCase.create(customerRequestDTO);

        verify(customerValidator).validateOnCreate(customer);
        verify(customerService).save(customer);
        verify(customerMapper).toEntity(customerRequestDTO);
        verify(customerMapper).toDTO(customer);

        ArgumentCaptor<CustomerCreatedEvent> eventCaptor = ArgumentCaptor.forClass(CustomerCreatedEvent.class);
        verify(customerProducer).publishCustomerCreated(eventCaptor.capture());

        CustomerCreatedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(customer.getId(), publishedEvent.customerId());
        assertEquals(customer.getName(), publishedEvent.name());
        assertEquals(customer.getLastName(), publishedEvent.lastName());
        assertEquals(customer.getEmail(), publishedEvent.email());

        assertEquals(customerResponseDTO, result);
    }

    @Test
    void shouldEditCustomerSuccessfully() {
        CustomerUpdateDTO customerUpdateDTO = new CustomerUpdateDTO(
                "YYYYY",
                "XXXXX",
                "00000000000",
                LocalDate.of(1990, 1, 1)
        );

        when(customerService.findById(customer.getId())).thenReturn(customer);
        doNothing().when(customerValidator).validateOnUpdate(customer.getId(), customerUpdateDTO);

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
        when(customerMapper.toDTO(any(Customer.class))).thenReturn(updatedDTO);

        CustomerResponseDTO result = customerUseCase.edit(customer.getId(), customerUpdateDTO);

        verify(customerService).findById(customer.getId());
        verify(customerValidator).validateOnUpdate(customer.getId(), customerUpdateDTO);
        verify(customerMapper).edit(customer, customerUpdateDTO);
        verify(customerService).edit(customer);
        verify(customerMapper).toDTO(any(Customer.class));

        ArgumentCaptor<CustomerUpdatedEvent> eventCaptor = ArgumentCaptor.forClass(CustomerUpdatedEvent.class);
        verify(customerProducer).publishCustomerUpdated(eventCaptor.capture());

        CustomerUpdatedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(customer.getId(), publishedEvent.customerId());
        assertEquals(customerUpdateDTO.name(), publishedEvent.name());
        assertEquals(customerUpdateDTO.lastName(), publishedEvent.lastName());

        assertEquals(customerUpdateDTO.name(), result.name());
        assertEquals(customerUpdateDTO.lastName(), result.lastName());
        assertEquals(customerUpdateDTO.cpf(), result.cpf());
    }

    @Test
    void shouldDeleteCustomerSuccessfully() {
        when(customerService.findById(customer.getId())).thenReturn(customer);

        customerUseCase.delete(customer.getId());

        verify(customerService).findById(customer.getId());

        verify(customerService).delete(customer);

        ArgumentCaptor<CustomerDeletedEvent> eventCaptor = ArgumentCaptor.forClass(CustomerDeletedEvent.class);
        verify(customerProducer).publishCustomerDeleted(eventCaptor.capture());

        CustomerDeletedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(customer.getId(), publishedEvent.customerId());
    }
}
