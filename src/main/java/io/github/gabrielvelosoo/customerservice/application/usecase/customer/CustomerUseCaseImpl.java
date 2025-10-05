package io.github.gabrielvelosoo.customerservice.application.usecase.customer;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CustomerUseCaseImpl implements CustomerUseCase {

    private static final Logger logger = LogManager.getLogger(CustomerUseCaseImpl.class);

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;
    private final CustomerValidator customerValidator;
    private final CustomerProducer customerProducer;

    @Override
    @Transactional
    public CustomerResponseDTO create(CustomerRequestDTO customerRequestDTO) {
        logger.debug("Mapping CustomerRequestDTO to entity for e-mail: '{}'", customerRequestDTO.email());
        Customer customer = customerMapper.toEntity(customerRequestDTO);
        logger.debug("Validating new customer data");
        customerValidator.validateOnCreate(customer);
        Customer savedCustomer = customerService.save(customer);
        logger.info("Customer persisted successfully with id: '{}'", savedCustomer.getId());
        logger.debug("Publishing 'CustomerCreatedEvent' for customer with id: '{}'", savedCustomer.getId());
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
        logger.debug("Editing customer id: '{}'", id);
        Customer customer = customerService.findById(id);
        logger.debug("Validating update for customer id: '{}'", id);
        customerValidator.validateOnUpdate(id, customerUpdateDTO);
        customerMapper.edit(customer, customerUpdateDTO);
        Customer editedCustomer = customerService.save(customer);
        logger.info("Customer id '{}' edited successfully", id);
        logger.debug("Publishing 'CustomerUpdatedEvent' for customer with id: '{}'", id);
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
        logger.debug("Deleting customer id: '{}'", id);
        Customer customer = customerService.findById(id);
        customerService.delete(customer);
        logger.info("Customer id '{}' deleted successfully", id);
        logger.debug("Publishing 'CustomerDeletedEvent' for customer with id: '{}'", id);
        customerProducer.publishCustomerDeleted(
                new CustomerDeletedEvent(
                        customer.getId()
                )
        );
    }
}
