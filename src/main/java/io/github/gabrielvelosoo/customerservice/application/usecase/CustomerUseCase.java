package io.github.gabrielvelosoo.customerservice.application.usecase;

import io.github.gabrielvelosoo.customerservice.application.dto.CustomerRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.CustomerResponseDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.CustomerUpdateDTO;

public interface CustomerUseCase {

    CustomerResponseDTO create(CustomerRequestDTO customerRequestDTO);
    CustomerResponseDTO edit(Long id, CustomerUpdateDTO customerUpdateDTO);
}
