package io.github.gabrielvelosoo.customerservice.application.mapper;

import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerResponseDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerUpdateDTO;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class CustomerMapper {

    public abstract Customer toEntity(CustomerRequestDTO customerRequestDTO);
    public abstract CustomerResponseDTO toDTO(Customer customer);

    public void edit(Customer customer, CustomerUpdateDTO customerUpdateDTO) {
        customer.setName(customerUpdateDTO.name());
        customer.setLastName(customerUpdateDTO.lastName());
        customer.setCpf(customerUpdateDTO.cpf());
        customer.setBirthDate(customerUpdateDTO.birthDate());
    }
}
