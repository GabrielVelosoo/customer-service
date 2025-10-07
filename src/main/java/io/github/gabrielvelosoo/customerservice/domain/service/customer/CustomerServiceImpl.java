package io.github.gabrielvelosoo.customerservice.domain.service.customer;

import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.repository.CustomerRepository;
import io.github.gabrielvelosoo.customerservice.infrastructure.exception.RecordNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LogManager.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;

    @Override
    public Customer save(Customer customer) {
        logger.debug("Saving customer with e-mail: '{}'", customer.getEmail());
        return customerRepository.save(customer);
    }

    @Override
    public Customer findById(Long customerId) {
        logger.debug("Searching for customer id: '{}'", customerId);
        return customerRepository.findById(customerId)
                .orElseThrow( () -> {
                    logger.warn("Customer not found with id: '{}'", customerId);
                    return new RecordNotFoundException("Customer not found: " + customerId);
                } );
    }

    @Override
    public Customer findByKeycloakUserId(String keycloakUserId) {
        logger.debug("Searching for customer with keycloakUserId: '{}'", keycloakUserId);
        return customerRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow( () -> {
                    logger.warn("Customer not found for keycloakUserId: '{}'", keycloakUserId);
                    return new RecordNotFoundException("Customer with this keycloakUserId not found: " + keycloakUserId);
                } );
    }

    @Override
    public void delete(Customer customer) {
        logger.debug("Deleting customer id: '{}'", customer.getId());
        customerRepository.delete(customer);
    }
}
