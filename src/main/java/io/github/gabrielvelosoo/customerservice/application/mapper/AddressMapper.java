package io.github.gabrielvelosoo.customerservice.application.mapper;

import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressResponseDTO;
import io.github.gabrielvelosoo.customerservice.domain.entity.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class AddressMapper {

    public abstract Address toEntity(AddressRequestDTO addressRequestDTO);
    public abstract AddressResponseDTO toDTO(Address address);
}
