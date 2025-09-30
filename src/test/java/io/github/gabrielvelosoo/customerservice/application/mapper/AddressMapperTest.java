package io.github.gabrielvelosoo.customerservice.application.mapper;

import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressRequestDTO;
import io.github.gabrielvelosoo.customerservice.domain.entity.Address;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressMapperTest {

    AddressMapper addressMapper;
    Address address;
    AddressRequestDTO addressRequestDTO;

    @BeforeEach
    void setUp() {
        addressMapper = new AddressMapperImpl();

        address = new Address(
                1L,
                "old",
                "test",
                "21563547665",
                "Quadra QR 205 Área Especial 2",
                "20",
                "Santa Maria",
                "Brasília",
                "Distrito Federal",
                "72505414",
                "",
                new Customer()
        );

        addressRequestDTO = new AddressRequestDTO(
                "new",
                "test",
                "31984537263",
                "Quadra SGAN 606 Módulo F",
                "11",
                "Asa Norte",
                "Brasília",
                "Distrito Federal",
                "70830256",
                ""
        );
    }

    @Nested
    class EditTests {

        @Test
        void shouldEditAddressSuccessfully() {
            addressMapper.edit(address, addressRequestDTO);

            assertEquals("new", address.getContactName());
            assertEquals("test", address.getContactLastName());
            assertEquals("31984537263", address.getContactPhone());
            assertEquals("Quadra SGAN 606 Módulo F", address.getAddress());
            assertEquals("11", address.getNumber());
            assertEquals("Asa Norte", address.getNeighborhood());
            assertEquals("Brasília", address.getCity());
            assertEquals("Distrito Federal", address.getState());
            assertEquals("70830256", address.getCep());
            assertEquals("", address.getComplement());
        }
    }
}