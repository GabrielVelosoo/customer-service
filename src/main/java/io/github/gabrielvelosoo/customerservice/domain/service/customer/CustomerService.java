package io.github.gabrielvelosoo.customerservice.domain.service.customer;

import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;

public interface CustomerService {

    Customer create(Customer customer);
    Customer findById(Long id);
    Customer edit(Customer customer);
}
