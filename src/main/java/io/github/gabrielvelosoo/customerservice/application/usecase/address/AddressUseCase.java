package io.github.gabrielvelosoo.customerservice.application.usecase.address;

import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressResponseDTO;

import java.util.List;

public interface AddressUseCase {

    AddressResponseDTO create(AddressRequestDTO addressRequestDTO);
    List<AddressResponseDTO> getAddressesLoggedCustomer();
    AddressResponseDTO edit(Long addressId, AddressRequestDTO addressRequestDTO);
    void delete(Long addressId);
}
