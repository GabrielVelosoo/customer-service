package io.github.gabrielvelosoo.customerservice.infrastructure.controller;

import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerResponseDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerUpdateDTO;
import io.github.gabrielvelosoo.customerservice.application.usecase.customer.CustomerUseCase;
import io.github.gabrielvelosoo.customerservice.application.validator.group.ValidationOrder;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController implements GenericController {

    private static final Logger logger = LogManager.getLogger(CustomerController.class);

    private final CustomerUseCase customerUseCase;

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(@RequestBody @Validated(ValidationOrder.class) CustomerRequestDTO customerRequestDTO) {
        logger.info("Received request to create new customer with e-mail: '{}'", customerRequestDTO.email());
        CustomerResponseDTO customerResponseDTO = customerUseCase.create(customerRequestDTO);
        logger.info("Customer created successfully with id: '{}'", customerResponseDTO.id());
        URI location = generateHeaderLocation(customerResponseDTO.id());
        return ResponseEntity.created(location).body(customerResponseDTO);
    }

    @PutMapping(value = "/{id}")
        public ResponseEntity<CustomerResponseDTO> edit(@PathVariable(name = "id") Long id,
                                                        @RequestBody @Validated(ValidationOrder.class) CustomerUpdateDTO customerUpdateDTO
    ) {
        logger.info("Received request to edit customer id: '{}'", id);
        CustomerResponseDTO customerResponseDTO = customerUseCase.edit(id, customerUpdateDTO);
        logger.info("Customer id '{}' updated successfully", id);
        return ResponseEntity.ok(customerResponseDTO);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long id) {
        logger.info("Received request to delete customer id: '{}'", id);
        customerUseCase.delete(id);
        logger.info("Customer id '{}' deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}
