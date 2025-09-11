package io.github.gabrielvelosoo.customerservice.domain.repository;

import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);
}
