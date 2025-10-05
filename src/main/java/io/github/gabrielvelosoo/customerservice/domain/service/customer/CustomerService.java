package io.github.gabrielvelosoo.customerservice.domain.service.customer;

import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;

public interface CustomerService {

    Customer save(Customer customer);
    Customer findById(Long id);
    Customer findByKeycloakUserId(String keycloakUserId);
    void delete(Customer customer);
}
