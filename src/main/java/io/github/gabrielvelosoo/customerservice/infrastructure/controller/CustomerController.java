package io.github.gabrielvelosoo.customerservice.infrastructure.controller;

import io.github.gabrielvelosoo.customerservice.application.dto.CustomerRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.CustomerResponseDTO;
import io.github.gabrielvelosoo.customerservice.application.usecase.CustomerUseCase;
import io.github.gabrielvelosoo.customerservice.application.validator.group.ValidationOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerUseCase customerUseCase;

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(@RequestBody @Validated(ValidationOrder.class) CustomerRequestDTO customerRequestDTO) {
        CustomerResponseDTO customerResponseDTO = customerUseCase.create(customerRequestDTO);
        return ResponseEntity.ok(customerResponseDTO);
    }
}
