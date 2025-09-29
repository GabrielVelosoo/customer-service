package io.github.gabrielvelosoo.customerservice.infrastructure.controller;

import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressResponseDTO;
import io.github.gabrielvelosoo.customerservice.application.usecase.address.AddressUseCase;
import io.github.gabrielvelosoo.customerservice.application.validator.group.ValidationOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController implements GenericController {

    private final AddressUseCase addressUseCase;

    @PostMapping
    public ResponseEntity<AddressResponseDTO> create(@RequestBody @Validated(ValidationOrder.class) AddressRequestDTO addressRequestDTO) {
        AddressResponseDTO addressResponseDTO = addressUseCase.create(addressRequestDTO);
        URI location = generateHeaderLocation(addressResponseDTO.id());
        return ResponseEntity.created(location).body(addressResponseDTO);
    }
}
