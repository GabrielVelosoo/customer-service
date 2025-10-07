package io.github.gabrielvelosoo.customerservice.infrastructure.controller;

import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.address.AddressResponseDTO;
import io.github.gabrielvelosoo.customerservice.application.usecase.address.AddressUseCase;
import io.github.gabrielvelosoo.customerservice.application.validator.group.ValidationOrder;
import io.github.gabrielvelosoo.customerservice.infrastructure.exception.model.ErrorResponse;
import io.github.gabrielvelosoo.customerservice.infrastructure.exception.model.ValidationErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Addresses")
public class AddressController implements GenericController {

    private static final Logger logger = LogManager.getLogger(AddressController.class);

    private final AddressUseCase addressUseCase;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "Create a new address for logged customer",
            description = "Registers a new address for the logged-in customer. Requires role USER or ADMIN. Returns the created address data and the Location header pointing to the new resource.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Address created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AddressResponseDTO.class)
                    ),
                    headers = @Header(
                            name = "Location",
                            description = "URI of the newly created address resource"
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized — invalid or expired access token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden — the authenticated user does not have permission to access this resource (role not allowed)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Unprocessable Entity — validation failed for one or more fields in the request body",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error — unexpected error occurred while processing the request. Contact the development team if it persists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<AddressResponseDTO> create(@RequestBody @Validated(ValidationOrder.class) AddressRequestDTO addressRequestDTO) {
        logger.info("Received request to create new address");
        AddressResponseDTO addressResponseDTO = addressUseCase.create(addressRequestDTO);
        logger.info("Address created succesfully with id: '{}'", addressResponseDTO.id());
        URI location = generateHeaderLocation(addressResponseDTO.id());
        return ResponseEntity.created(location).body(addressResponseDTO);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "Get addresses of logged customer",
            description = "Retrieves all addresses belonging to the logged-in customer. Requires role USER or ADMIN. Returns a list of address data.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Addresses retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = AddressResponseDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized — invalid or expired access token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error — unexpected error occurred while processing the request. Contact the development team if it persists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<List<AddressResponseDTO>> getAddressesLoggedCustomer() {
        logger.info("Received request to get addresses of logged customer");
        List<AddressResponseDTO> addressResponseDTOs = addressUseCase.getAddressesLoggedCustomer();
        logger.info("Addresses get successfully");
        return ResponseEntity.ok(addressResponseDTOs);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "Edit an address",
            description = "Edit an address. Requires role USER or ADMIN. Returns the updated address data.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Address edited successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AddressResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized — invalid or expired access token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found — no address exists with the provided ID",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Unprocessable Entity — validation failed for one or more fields in the request body",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error — unexpected error occurred while processing the request. Contact the development team if it persists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
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
    @Operation(
            summary = "Delete an address",
            description = "Delete an address. Requires role USER or ADMIN.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Address deleted successfully. No content is returned in the response body"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized — invalid or expired access token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found — no address exists with the provided ID",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error — unexpected error occurred while processing the request. Contact the development team if it persists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long addressId) {
        logger.info("Received request to delete address with id: '{}'", addressId);
        addressUseCase.delete(addressId);
        logger.info("Address id '{}' deleted successfully", addressId);
        return ResponseEntity.noContent().build();
    }
}
