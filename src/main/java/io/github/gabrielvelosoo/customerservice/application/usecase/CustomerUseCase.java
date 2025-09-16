package io.github.gabrielvelosoo.customerservice.application.usecase;

import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerResponseDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerUpdateDTO;

public interface CustomerUseCase {

    CustomerResponseDTO create(CustomerRequestDTO customerRequestDTO);
    CustomerResponseDTO edit(Long id, CustomerUpdateDTO customerUpdateDTO);
    void delete(Long id);
}
