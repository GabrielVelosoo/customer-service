package io.github.gabrielvelosoo.customerservice.domain.repository;

import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class CustomerRepositoryTest {

    @Autowired
    CustomerRepository customerRepository;

    Customer customer1;
    Customer customer2;

    @BeforeEach
    void setUp() {
        customer1 = new Customer(
                null,
                "YYYYY",
                "XXXXX",
                UUID.randomUUID().toString(),
                "abcd@example.com",
                "00000000000",
                "77777777",
                LocalDate.of(1990, 2, 23)
        );
        customer1 = customerRepository.saveAndFlush(customer1);

        customer2 = new Customer(
                null,
                "XXXXX",
                "YYYYY",
                UUID.randomUUID().toString(),
                "dcba@example.com",
                "11111111111",
                "55555555",
                LocalDate.of(2000, 5, 15)
        );
        customer2 = customerRepository.saveAndFlush(customer2);
    }

    @Test
    @DisplayName("Should get Customer successfully from database")
    void shouldFindByCpfAndNotIdSuccessfully() {
        Optional<Customer> foundedCustomer = customerRepository.findByCpfAndNotId(customer2.getCpf(), customer1.getId());

        assertTrue(foundedCustomer.isPresent(), "Should find another customer with same CPF");
        assertEquals(customer2.getId(), foundedCustomer.get().getId(), "The found customer should be c2");
    }
}