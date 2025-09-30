package io.github.gabrielvelosoo.customerservice.application.usecase.address;

import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressResponseDTO;
import io.github.gabrielvelosoo.customerservice.application.mapper.AddressMapper;
import io.github.gabrielvelosoo.customerservice.application.validator.custom.AddressValidator;
import io.github.gabrielvelosoo.customerservice.domain.entity.Address;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.service.address.AddressService;
import io.github.gabrielvelosoo.customerservice.domain.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AddressUseCaseImpl implements AddressUseCase {

    private final AddressService addressService;
    private final AuthService authService;
    private final AddressMapper addressMapper;
    private final AddressValidator addressValidator;

    @Override
    public AddressResponseDTO create(AddressRequestDTO addressRequestDTO) {
        Address address = addressMapper.toEntity(addressRequestDTO);
        Customer customer = authService.getLoggedCustomer();
        address.setCustomer(customer);
        addressValidator.validateOnCreate(address);
        Address savedAddress = addressService.create(address);
        return addressMapper.toDTO(savedAddress);
    }

    @Override
    public List<AddressResponseDTO> getAddressesLoggedCustomer() {
        Customer customer = authService.getLoggedCustomer();
        List<Address> addresses = addressService.getAddressesLoggedCustomer(customer.getId());
        return addressMapper.toDTOs(addresses);
    }

    @Override
    public AddressResponseDTO edit(Long addressId, AddressRequestDTO addressRequestDTO) {
        Address address = addressService.findById(addressId);
        addressValidator.validateOnUpdateAndDelete(address);
        addressMapper.edit(address, addressRequestDTO);
        Address updatedAddress = addressService.edit(address);
        return addressMapper.toDTO(updatedAddress);
    }

    @Override
    public void delete(Long addressId) {
        Address address = addressService.findById(addressId);
        addressValidator.validateOnUpdateAndDelete(address);
        addressService.delete(address);
    }
}
