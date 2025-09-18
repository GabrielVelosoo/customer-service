package io.github.gabrielvelosoo.customerservice.domain.service;

import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;

public interface CustomerService {

    Customer save(Customer customer);
    Customer findById(Long id);
    Customer edit(Customer customer);
    void delete(Customer customer);
}
