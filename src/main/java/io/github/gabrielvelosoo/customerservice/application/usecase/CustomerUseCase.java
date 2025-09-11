package io.github.gabrielvelosoo.customerservice.application.usecase;

import io.github.gabrielvelosoo.customerservice.application.dto.CustomerRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.CustomerResponseDTO;

public interface CustomerUseCase {

    CustomerResponseDTO create(CustomerRequestDTO customerRequestDTO);
}
