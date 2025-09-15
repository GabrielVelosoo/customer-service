package io.github.gabrielvelosoo.customerservice.application.validator.custom;

import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.repository.CustomerRepository;
import io.github.gabrielvelosoo.customerservice.infrastructure.exception.DuplicateRecordException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomerValidator {

    private final CustomerRepository customerRepository;

    public void validate(Customer customer) {
        if(customerHasRegisteredEmail(customer)) {
            throw new DuplicateRecordException("Email already registered");
        }
        if(customerHasRegisteredCpf(customer)) {
            throw new DuplicateRecordException("CPF already registered");
        }
    }

    private boolean customerHasRegisteredEmail(Customer customer) {
        Optional<Customer> foundCustomer = customerRepository.findByEmail(customer.getEmail());
        if(customer.getId() == null) {
            return foundCustomer.isPresent();
        }
        return foundCustomer
                .map(c -> !c.getId().equals(customer.getId()))
                .orElse(false);
    }

    private boolean customerHasRegisteredCpf(Customer customer) {
        Optional<Customer> foundCustomer = customerRepository.findByCpf(customer.getCpf());
        if(customer.getId() == null) {
            return foundCustomer.isPresent();
        }
        return foundCustomer
                .map(c -> !c.getId().equals(customer.getId()))
                .orElse(false);
    }
}
