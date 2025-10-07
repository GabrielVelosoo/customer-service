package io.github.gabrielvelosoo.customerservice.infrastructure.controller;

import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressResponseDTO;
import io.github.gabrielvelosoo.customerservice.application.usecase.address.AddressUseCase;
import io.github.gabrielvelosoo.customerservice.application.validator.group.ValidationOrder;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController implements GenericController {

    private static final Logger logger = LogManager.getLogger(AddressController.class);

    private final AddressUseCase addressUseCase;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AddressResponseDTO> create(@RequestBody @Validated(ValidationOrder.class) AddressRequestDTO addressRequestDTO) {
        logger.info("Received request to create new address");
        AddressResponseDTO addressResponseDTO = addressUseCase.create(addressRequestDTO);
        logger.info("Address created succesfully with id: '{}'", addressResponseDTO.id());
        URI location = generateHeaderLocation(addressResponseDTO.id());
        return ResponseEntity.created(location).body(addressResponseDTO);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<AddressResponseDTO>> getAddressesLoggedCustomer() {
        logger.info("Received request to get addresses of logged customer");
        List<AddressResponseDTO> addressResponseDTOs = addressUseCase.getAddressesLoggedCustomer();
        logger.info("Addresses get successfully");
        return ResponseEntity.ok(addressResponseDTOs);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AddressResponseDTO> edit(@PathVariable(name = "id") Long addressId,
                                                   @RequestBody @Validated(ValidationOrder.class) AddressRequestDTO addressRequestDTO
    ) {
        logger.info("Received request to edit address with id: '{}'", addressId);
        AddressResponseDTO addressResponseDTO = addressUseCase.edit(addressId, addressRequestDTO);
        logger.info("Address id '{}' updated successfully", addressResponseDTO.id());
        return ResponseEntity.ok(addressResponseDTO);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long addressId) {
        logger.info("Received request to delete address with id: '{}'", addressId);
        addressUseCase.delete(addressId);
        logger.info("Address id '{}' deleted successfully", addressId);
        return ResponseEntity.noContent().build();
    }
}
