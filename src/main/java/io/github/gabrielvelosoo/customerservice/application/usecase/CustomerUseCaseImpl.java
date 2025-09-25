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
import io.github.gabrielvelosoo.customerservice.domain.service.customer.CustomerService;
import io.github.gabrielvelosoo.customerservice.infrastructure.messaging.producer.CustomerProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerUseCaseImpl implements CustomerUseCase {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;
    private final CustomerValidator customerValidator;
    private final CustomerProducer customerProducer;

    @Override
    @Transactional
    public CustomerResponseDTO create(CustomerRequestDTO customerRequestDTO) {
        Customer customer = customerMapper.toEntity(customerRequestDTO);
        customerValidator.validateOnCreate(customer);
        Customer savedCustomer = customerService.save(customer);
        customerProducer.publishCustomerCreated(
                new CustomerCreatedEvent(
                        savedCustomer.getId(),
                        savedCustomer.getName(),
                        savedCustomer.getLastName(),
                        customerRequestDTO.email(),
                        customerRequestDTO.password()
                )
        );
        return customerMapper.toDTO(savedCustomer);
    }

    @Override
    @Transactional
    public CustomerResponseDTO edit(Long id, CustomerUpdateDTO customerUpdateDTO) {
        Customer customer = customerService.findById(id);
        customerValidator.validateOnUpdate(id, customerUpdateDTO);
        customerMapper.edit(customer, customerUpdateDTO);
        Customer editedCustomer = customerService.edit(customer);
        customerProducer.publishCustomerUpdated(
                new CustomerUpdatedEvent(
                        editedCustomer.getId(),
                        editedCustomer.getName(),
                        editedCustomer.getLastName()
                )
        );
        return customerMapper.toDTO(editedCustomer);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Customer customer = customerService.findById(id);
        customerService.delete(customer);
        customerProducer.publishCustomerDeleted(
                new CustomerDeletedEvent(
                        customer.getId()
                )
        );
    }
}
