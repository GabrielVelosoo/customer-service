package io.github.gabrielvelosoo.customerservice.domain.repository;

import io.github.gabrielvelosoo.customerservice.domain.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
