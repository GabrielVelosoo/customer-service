package io.github.gabrielvelosoo.customerservice.domain.repository;

import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
