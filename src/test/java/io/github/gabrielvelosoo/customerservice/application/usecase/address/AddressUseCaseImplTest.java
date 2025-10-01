package io.github.gabrielvelosoo.customerservice.application.usecase.address;

import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressResponseDTO;
import io.github.gabrielvelosoo.customerservice.application.mapper.AddressMapper;
import io.github.gabrielvelosoo.customerservice.application.validator.custom.AddressValidator;
import io.github.gabrielvelosoo.customerservice.domain.entity.Address;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.service.address.AddressService;
import io.github.gabrielvelosoo.customerservice.domain.service.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressUseCaseImplTest {

    @Mock
    AddressService addressService;

    @Mock
    AuthService authService;

    @Mock
    AddressMapper addressMapper;

    @Mock
    AddressValidator addressValidator;

    @InjectMocks
    AddressUseCaseImpl addressUseCase;

    Customer customer;
    Address address;
    AddressRequestDTO addressRequestDTO;
    AddressResponseDTO addressResponseDTO;

    @BeforeEach
    void setUp() {
        customer = new Customer(
                1L,
                "unit",
                "test",
                UUID.randomUUID().toString(),
                "test@example.com",
                "00000000000",
                "00000000",
                LocalDate.of(1990, 2, 23)
        );

        address = new Address(
                1L,
                "unit",
                "test",
                "21563547665",
                "Quadra QR 205 Área Especial 2",
                "20",
                "Santa Maria",
                "Brasília",
                "Distrito Federal",
                "72505414",
                "",
                customer
        );

        addressRequestDTO = new AddressRequestDTO(
                "unit",
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

        addressResponseDTO = new AddressResponseDTO(
                1L,
                "unit",
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
    class CreateTests {

        @Test
        void shouldCreateAddressSuccessfully() {
            when(authService.getLoggedCustomer()).thenReturn(customer);
            when(addressMapper.toEntity(addressRequestDTO)).thenReturn(address);
            when(addressService.save(address)).thenReturn(address);
            when(addressMapper.toDTO(address)).thenReturn(addressResponseDTO);

            AddressResponseDTO result = addressUseCase.create(addressRequestDTO);

            assertNotNull(result);
            assertEquals(addressResponseDTO, result);

            verify(authService, times(1)).getLoggedCustomer();
            verify(addressMapper, times(1)).toEntity(addressRequestDTO);
            verify(addressService, times(1)).save(address);
            verify(addressValidator, times(1)).validateOnCreate(address);
            verify(addressMapper, times(1)).toDTO(address);
        }
    }

    @Nested
    class GetAddressesLoggedCustomerTests {

        @Test
        void shouldGetAddressesLoggedCustomerSuccessfully() {
            when(authService.getLoggedCustomer()).thenReturn(customer);
            when(addressService.getAddressesLoggedCustomer(customer.getId())).thenReturn(Collections.singletonList(address));
            when(addressMapper.toDTOs(Collections.singletonList(address))).thenReturn(Collections.singletonList(addressResponseDTO));

            List<AddressResponseDTO> result = addressUseCase.getAddressesLoggedCustomer();

            assertEquals(1, result.size());
            assertEquals(addressResponseDTO, result.getFirst());

            verify(authService, times(1)).getLoggedCustomer();
            verify(addressService, times(1)).getAddressesLoggedCustomer(customer.getId());
            verify(addressMapper, times(1)).toDTOs(Collections.singletonList(address));
        }
    }

    @Nested
    class EditTests {

        @Test
        void shouldEditAddressSuccessfully() {
            when(addressService.findById(1L)).thenReturn(address);
            when(addressService.edit(address)).thenReturn(address);
            when(addressMapper.toDTO(address)).thenReturn(addressResponseDTO);

            AddressResponseDTO result = addressUseCase.edit(1L, addressRequestDTO);

            assertNotNull(result);
            assertEquals(addressResponseDTO, result);

            verify(addressService, times(1)).findById(1L);
            verify(addressValidator, times(1)).validateOnUpdateAndDelete(address);
            verify(addressMapper, times(1)).edit(address, addressRequestDTO);
            verify(addressService, times(1)).edit(address);
            verify(addressMapper, times(1)).toDTO(address);
        }
    }

    @Nested
    class DeleteTests {

        @Test
        void shouldDeleteAddressSuccessfully() {
            when(addressService.findById(1L)).thenReturn(address);

            addressUseCase.delete(1L);

            verify(addressService, times(1)).findById(1L);
            verify(addressValidator, times(1)).validateOnUpdateAndDelete(address);
            verify(addressService, times(1)).delete(address);
        }
    }
}