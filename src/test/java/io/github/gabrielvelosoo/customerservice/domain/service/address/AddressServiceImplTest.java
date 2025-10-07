package io.github.gabrielvelosoo.customerservice.domain.service.address;

import io.github.gabrielvelosoo.customerservice.domain.entity.Address;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.repository.AddressRepository;
import io.github.gabrielvelosoo.customerservice.domain.repository.CustomerRepository;
import io.github.gabrielvelosoo.customerservice.infrastructure.exception.RecordNotFoundException;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    AddressRepository addressRepository;

    @Mock
    CustomerRepository customerRepository;

    @InjectMocks
    AddressServiceImpl addressService;

    Customer customer;
    Address address;

    @BeforeEach
    void setUp() {
        customer = new Customer(
                1L,
                "test1",
                "unit",
                UUID.randomUUID().toString(),
                "test1@example.com",
                "00000000000",
                "00000000",
                LocalDate.of(1990, 2, 23)
        );

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
                customer
        );
    }

    @Nested
    class createTests {

        @Test
        void shouldCreateAddressSuccessfully() {
            when(addressRepository.save(address)).thenReturn(address);

            Address savedAddress = addressService.save(address);

            assertNotNull(savedAddress);
            assertEquals(1L, savedAddress.getId());

            verify(addressRepository, times(1)).save(address);
        }
    }

    @Nested
    class FindByIdTests {

        @Test
        void shouldFindAddressByIdSuccessfully() {
            when(addressRepository.findById(1L)).thenReturn(Optional.of(address));

            Address foundAddress = addressService.findById(1L);

            assertNotNull(foundAddress);
            assertEquals(1L, foundAddress.getId());

            verify(addressRepository, times(1)).findById(1L);
        }

        @Test
        void shouldThrowExceptionWhenAddressDoesNotExist() {
            when(addressRepository.findById(1L)).thenReturn(Optional.empty());

            RecordNotFoundException e = assertThrows(
                    RecordNotFoundException.class,
                    () -> addressService.findById(1L)
            );

            assertEquals("Address not found: 1", e.getMessage());
            verify(addressRepository, times(1)).findById(1L);
        }
    }

    @Nested
    class GetAddressesLoggedCustomerTests {

        @Test
        void shouldGetAddressesLoggedCustomerWhenCustomerExistsSuccessfully() {
            when(customerRepository.existsById(1L)).thenReturn(true);
            when(addressRepository.findByCustomerId(1L)).thenReturn(Collections.singletonList(address));

            List<Address> result = addressService.getAddressesLoggedCustomer(1L);

            assertEquals(1, result.size());
            assertEquals(address, result.getFirst());

            verify(customerRepository, times(1)).existsById(1L);
            verify(addressRepository, times(1)).findByCustomerId(1L);
        }

        @Test
        void shouldThrowExceptionGetAddressesLoggedCustomerWhenCustomerDoesNotExists() {
            when(customerRepository.existsById(1L)).thenReturn(false);

            RecordNotFoundException exception = assertThrows(
                    RecordNotFoundException.class,
                    () -> addressService.getAddressesLoggedCustomer(1L)
            );

            assertEquals("Customer not found: 1", exception.getMessage());
            verify(customerRepository, times(1)).existsById(1L);
        }
    }

    @Nested
    class EditTests {

        @Test
        void shouldSaveEditedAddressSuccessfully() {
            when(addressRepository.save(address)).thenReturn(address);

            Address editedAddress = addressService.save(address);

            assertNotNull(editedAddress);
            assertEquals("Gabriel", editedAddress.getContactName());

            verify(addressRepository, times(1)).save(address);
        }
    }

    @Nested
    class DeleteTests {

        @Test
        void shouldDeleteAddressUsingRepositorySuccessfully() {
            doNothing().when(addressRepository).delete(address);
            addressService.delete(address);
            verify(addressRepository, times(1)).delete(address);
        }
    }
}