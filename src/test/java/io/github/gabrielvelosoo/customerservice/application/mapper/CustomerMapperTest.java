package io.github.gabrielvelosoo.customerservice.application.mapper;

import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerUpdateDTO;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerMapperTest {

    CustomerMapper customerMapper;
    Customer customer;
    CustomerUpdateDTO customerUpdateDTO;

    @BeforeEach
    void setUp() {
        customerMapper = new CustomerMapperImpl();

        customer = new Customer(
                1L,
                "Old",
                "Name",
                UUID.randomUUID().toString(),
                "abcd@example.com",
                "85656417085",
                "76964604",
                LocalDate.of(2004, 4, 10)
        );

        customerUpdateDTO = new CustomerUpdateDTO(
                "New",
                "Last",
                "12345678900",
                LocalDate.of(1995, 5, 20)
        );
    }

    @Test
    void shouldEditCustomerWithUpdateDTO() {
        customerMapper.edit(customer, customerUpdateDTO);

        assertEquals("New", customer.getName());
        assertEquals("Last", customer.getLastName());
        assertEquals("12345678900", customer.getCpf());
        assertEquals(LocalDate.of(1995, 5, 20), customer.getBirthDate());
    }
}