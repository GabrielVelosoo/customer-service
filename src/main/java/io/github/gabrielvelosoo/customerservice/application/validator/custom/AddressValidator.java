package io.github.gabrielvelosoo.customerservice.application.validator.custom;

import io.github.gabrielvelosoo.customerservice.domain.entity.Address;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.repository.CustomerRepository;
import io.github.gabrielvelosoo.customerservice.infrastructure.exception.RecordNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddressValidator {

    private final CustomerRepository customerRepository;

    public void validateOnCreate(Address address) {
        if(!customerExists(address.getCustomer())) {
            throw new RecordNotFoundException("Customer not found: " + address.getCustomer().getId());
        }
    }

    private boolean customerExists(Customer customer) {
        if(customer == null || customer.getId() == null) {
            return false;
        }
        return customerRepository.existsById(customer.getId());
    }
}
