package io.github.gabrielvelosoo.customerservice.application.validator.custom;

import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerUpdateDTO;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.repository.CustomerRepository;
import io.github.gabrielvelosoo.customerservice.infrastructure.exception.DuplicateRecordException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerValidator {

    private static final Logger logger = LogManager.getLogger(CustomerValidator.class);

    private final CustomerRepository customerRepository;

    public void validateOnCreate(Customer customer) {
        logger.debug("Validating customer creation for e-mail: '{}'", customer.getEmail());
        if(customerHasRegisteredEmail(customer.getEmail())) {
            logger.warn("Duplicate email detected: '{}'", customer.getEmail());
            throw new DuplicateRecordException("There is already an account registered with this e-mail");
        }
        if(customerHasRegisteredCpf(customer.getCpf())) {
            logger.warn("Duplicate CPF detected: '{}'", customer.getCpf());
            throw new DuplicateRecordException("There is already an account registered with this CPF");
        }
    }

    public void validateOnUpdate(Long customerId, CustomerUpdateDTO customerUpdateDTO) {
        logger.debug("Validating customer update for id: '{}'", customerId);
        if(customerHasRegisteredCpfOnUpdate(customerId, customerUpdateDTO.cpf())) {
            logger.warn("Duplicate CPF detected during update for id: '{}'", customerId);
            throw new DuplicateRecordException("There is already an account registered with this CPF");
        }
    }

    private boolean customerHasRegisteredEmail(String email) {
        return customerRepository.findByEmail(email).isPresent();
    }

    private boolean customerHasRegisteredCpf(String cpf) {
        return customerRepository.findByCpf(cpf).isPresent();
    }

    private boolean customerHasRegisteredCpfOnUpdate(Long customerId, String cpf) {
        return customerRepository.findByCpfAndNotId(cpf, customerId).isPresent();
    }
}
