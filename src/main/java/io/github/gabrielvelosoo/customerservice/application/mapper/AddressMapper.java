package io.github.gabrielvelosoo.customerservice.application.mapper;

import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressResponseDTO;
import io.github.gabrielvelosoo.customerservice.domain.entity.Address;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class AddressMapper {

    public abstract Address toEntity(AddressRequestDTO addressRequestDTO);
    public abstract AddressResponseDTO toDTO(Address address);
    public abstract List<AddressResponseDTO> toDTOs(List<Address> addresses);

    public void edit(Address address, AddressRequestDTO addressRequestDTO) {
        address.setContactName(addressRequestDTO.contactName());
        address.setContactLastName(addressRequestDTO.contactLastName());
        address.setContactPhone(addressRequestDTO.contactPhone());
        address.setAddress(addressRequestDTO.address());
        address.setNumber(addressRequestDTO.number());
        address.setNeighborhood(addressRequestDTO.neighborhood());
        address.setCity(addressRequestDTO.city());
        address.setState(addressRequestDTO.state());
        address.setCep(addressRequestDTO.cep());
        address.setComplement(addressRequestDTO.complement());
    }
}
