package io.github.gabrielvelosoo.customerservice.application.validator.custom;

import io.github.gabrielvelosoo.customerservice.domain.entity.Address;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.repository.CustomerRepository;
import io.github.gabrielvelosoo.customerservice.domain.service.auth.AuthService;
import io.github.gabrielvelosoo.customerservice.infrastructure.exception.BusinessRuleException;
import io.github.gabrielvelosoo.customerservice.infrastructure.exception.RecordNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddressValidator {

    private static final Logger logger = LogManager.getLogger(AddressValidator.class);

    private final CustomerRepository customerRepository;
    private final AuthService authService;

    public void validateOnCreate(Address address) {
        logger.debug("Validating address creation");
        if(customerDoesNotExist(address.getCustomer())) {
            logger.warn("Customer with id '{}' does not exist during address creation", address.getCustomer().getId());
            throw new RecordNotFoundException("Customer not found: " + address.getCustomer().getId());
        }
    }

    public void validateOnUpdateAndDelete(Address address) {
        logger.debug("Validating address update/delete for address id: '{}'", address.getId());
        if(customerDoesNotExist(address.getCustomer())) {
            Long customerId = address.getCustomer() != null ? address.getCustomer().getId() : null;
            logger.warn("Attempt to update/delete address '{}' with non-existent customer id: '{}'",
                    address.getId(), customerId);
            throw new RecordNotFoundException("Customer not found: " + customerId);
        }
        if(!addressBelongsToLoggedCustomer(address)) {
            Long loggedId = authService.getLoggedCustomer() != null ? authService.getLoggedCustomer().getId() : null;
            Long ownerId = address.getCustomer() != null ? address.getCustomer().getId() : null;
            logger.warn("Unauthorized access: Logged user '{}' tried to manage address '{}' belonging to customer '{}'",
                    loggedId, address.getId(), ownerId);
            throw new BusinessRuleException("You don’t have permission to manage this address");
        }
    }

    private boolean customerDoesNotExist(Customer customer) {
        if(customer == null || customer.getId() == null) {
            return true;
        }
        return !customerRepository.existsById(customer.getId());
    }

    private boolean addressBelongsToLoggedCustomer(Address address) {
        Customer customer = authService.getLoggedCustomer();
        return customer != null
                && address.getCustomer() != null
                && customer.getId().equals(address.getCustomer().getId());
    }
}
