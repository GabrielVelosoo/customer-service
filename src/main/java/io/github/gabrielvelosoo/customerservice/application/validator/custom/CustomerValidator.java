package io.github.gabrielvelosoo.customerservice.application.validator.custom;

import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerUpdateDTO;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.repository.CustomerRepository;
import io.github.gabrielvelosoo.customerservice.infrastructure.exception.DuplicateRecordException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerValidator {

    private final CustomerRepository customerRepository;

    public void validateOnCreate(Customer customer) {
        if(customerHasRegisteredEmail(customer.getEmail())) {
            throw new DuplicateRecordException("There is already an account registered with this email");
        }
        if(customerHasRegisteredCpf(customer.getCpf())) {
            throw new DuplicateRecordException("There is already an account registered with this CPF");
        }
    }

    private boolean customerHasRegisteredEmail(String email) {
        return customerRepository.findByEmail(email).isPresent();
    }

    private boolean customerHasRegisteredCpf(String cpf) {
        return customerRepository.findByCpf(cpf).isPresent();
    }

    public void validateOnUpdate(Long customerId, CustomerUpdateDTO customerUpdateDTO) {
        if(customerHasRegisteredCpfOnUpdate(customerId, customerUpdateDTO.cpf())) {
            throw new DuplicateRecordException("There is already an account registered with this CPF");
        }
    }

    private boolean customerHasRegisteredCpfOnUpdate(Long customerId, String cpf) {
        return customerRepository.findByCpfAndNotId(cpf, customerId).isPresent();
    }
}
