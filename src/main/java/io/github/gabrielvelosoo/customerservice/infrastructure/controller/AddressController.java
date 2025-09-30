package io.github.gabrielvelosoo.customerservice.infrastructure.controller;

import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressResponseDTO;
import io.github.gabrielvelosoo.customerservice.application.usecase.address.AddressUseCase;
import io.github.gabrielvelosoo.customerservice.application.validator.group.ValidationOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> getAddressesLoggedCustomer() {
        List<AddressResponseDTO> addressResponseDTOs = addressUseCase.getAddressesLoggedCustomer();
        return ResponseEntity.ok(addressResponseDTOs);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<AddressResponseDTO> edit(@PathVariable(name = "id") Long addressId,
                                                   @RequestBody @Validated(ValidationOrder.class) AddressRequestDTO addressRequestDTO
    ) {
        AddressResponseDTO addressResponseDTO = addressUseCase.edit(addressId, addressRequestDTO);
        return ResponseEntity.ok(addressResponseDTO);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long addressId) {
        addressUseCase.delete(addressId);
        return ResponseEntity.noContent().build();
    }
}
