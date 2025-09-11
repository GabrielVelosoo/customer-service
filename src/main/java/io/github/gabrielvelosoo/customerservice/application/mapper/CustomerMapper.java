package io.github.gabrielvelosoo.customerservice.application.mapper;

import io.github.gabrielvelosoo.customerservice.application.dto.CustomerRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.CustomerResponseDTO;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer toEntity(CustomerRequestDTO customerRequestDTO);
    CustomerResponseDTO toDTO(Customer customer);
}
