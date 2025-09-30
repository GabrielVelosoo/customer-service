package io.github.gabrielvelosoo.customerservice.application.validator.custom;

import io.github.gabrielvelosoo.customerservice.domain.entity.Address;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.repository.CustomerRepository;
import io.github.gabrielvelosoo.customerservice.domain.service.auth.AuthService;
import io.github.gabrielvelosoo.customerservice.infrastructure.exception.BusinessRuleException;
import io.github.gabrielvelosoo.customerservice.infrastructure.exception.RecordNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddressValidator {

    private final CustomerRepository customerRepository;
    private final AuthService authService;

    public void validateOnCreate(Address address) {
        if(customerDoesNotExist(address.getCustomer())) {
            throw new RecordNotFoundException("Customer not found: " + address.getCustomer().getId());
        }
    }

    public void validateOnUpdateAndDelete(Address address) {
        if(customerDoesNotExist(address.getCustomer())) {
            throw new RecordNotFoundException("Customer not found: " + address.getCustomer().getId());
        }
        if(!addressBelongsToLoggedCustomer(address)) {
            throw new BusinessRuleException("You don´t have permission to manage this address");
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
