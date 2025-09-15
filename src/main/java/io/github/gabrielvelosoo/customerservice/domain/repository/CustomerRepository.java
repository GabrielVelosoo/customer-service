package io.github.gabrielvelosoo.customerservice.domain.repository;

import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByCpf(String cpf);

    @Query("SELECT c FROM Customer c WHERE c.cpf = :cpf AND c.id <> :id")
    Optional<Customer> findByCpfAndNotId(@Param("cpf") String cpf, @Param("id") Long id);
}
