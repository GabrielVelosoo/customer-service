package io.github.gabrielvelosoo.customerservice.infrastructure.controller;

import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerResponseDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerUpdateDTO;
import io.github.gabrielvelosoo.customerservice.application.usecase.customer.CustomerUseCase;
import io.github.gabrielvelosoo.customerservice.application.validator.group.ValidationOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController implements GenericController {

    private final CustomerUseCase customerUseCase;

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(@RequestBody @Validated(ValidationOrder.class) CustomerRequestDTO customerRequestDTO) {
        CustomerResponseDTO customerResponseDTO = customerUseCase.create(customerRequestDTO);
        URI location = generateHeaderLocation(customerResponseDTO.id());
        return ResponseEntity.created(location).body(customerResponseDTO);
    }

    @PutMapping(value = "/{id}")
        public ResponseEntity<CustomerResponseDTO> edit(@PathVariable(name = "id") Long id,
                @RequestBody @Validated(ValidationOrder.class) CustomerUpdateDTO customerUpdateDTO
    ) {
            CustomerResponseDTO customerResponseDTO = customerUseCase.edit(id, customerUpdateDTO);
            return ResponseEntity.ok(customerResponseDTO);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        customerUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
