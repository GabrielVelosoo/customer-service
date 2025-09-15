package io.github.gabrielvelosoo.customerservice.domain.service.customer;

import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.repository.CustomerRepository;
import io.github.gabrielvelosoo.customerservice.infrastructure.exception.RecordNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Customer create(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow( () -> new RecordNotFoundException("Record not found") );
    }

    @Override
    public Customer edit(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public void delete(Customer customer) {
        customerRepository.delete(customer);
    }
}
