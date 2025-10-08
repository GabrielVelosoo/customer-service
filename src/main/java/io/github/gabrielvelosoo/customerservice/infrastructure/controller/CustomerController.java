package io.github.gabrielvelosoo.customerservice.infrastructure.controller;

import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerRequestDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerResponseDTO;
import io.github.gabrielvelosoo.customerservice.application.dto.customer.CustomerUpdateDTO;
import io.github.gabrielvelosoo.customerservice.application.usecase.customer.CustomerUseCase;
import io.github.gabrielvelosoo.customerservice.application.validator.group.ValidationOrder;
import io.github.gabrielvelosoo.customerservice.infrastructure.exception.model.ErrorResponse;
import io.github.gabrielvelosoo.customerservice.infrastructure.exception.model.ValidationErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
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

@RestController
@RequestMapping(value = "/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers")
public class CustomerController implements GenericController {

    private static final Logger logger = LogManager.getLogger(CustomerController.class);

    private final CustomerUseCase customerUseCase;

    @PostMapping
    @Operation(
            summary = "Create a new customer",
            description = "Registers a new customer ensuring unique e-mail and CPF. Publishes an event to create the corresponding user in the authentication service. Returns the created customer data and the resource location."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Customer created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomerResponseDTO.class)
                    ),
                    headers = @Header(
                            name = "Location",
                            description = "URI of the newly created address resource"
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict — the e-mail or CPF provided is already registered with another account",
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
                    description = "Internal Server Error — an unexpected error occurred while communicating with Keycloak (e.g., creating, updating, or deleting a user, assigning roles, or validating tokens). Contact the development team if the issue persists.",
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
    public ResponseEntity<CustomerResponseDTO> create(@RequestBody @Validated(ValidationOrder.class) CustomerRequestDTO customerRequestDTO) {
        logger.info("Received request to create new customer with e-mail: '{}'", customerRequestDTO.email());
        CustomerResponseDTO customerResponseDTO = customerUseCase.create(customerRequestDTO);
        logger.info("Customer created successfully with id: '{}'", customerResponseDTO.id());
        URI location = generateHeaderLocation(customerResponseDTO.id());
        return ResponseEntity.created(location).body(customerResponseDTO);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "Edit a customer",
            description = "Edit a customer. Requires role USER or ADMIN. Publishes an event to edit the corresponding user in the authentication service. Returns the updated customer data.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer edited successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomerResponseDTO.class)
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
                    description = "Not Found — no customer exists with the provided ID",
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
                    description = "Internal Server Error — an unexpected error occurred while communicating with Keycloak (e.g., creating, updating, or deleting a user, assigning roles, or validating tokens). Contact the development team if the issue persists.",
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
    public ResponseEntity<CustomerResponseDTO> edit(@PathVariable(name = "id") Long customerId,
                                                    @RequestBody @Validated(ValidationOrder.class) CustomerUpdateDTO customerUpdateDTO
    ) {
        logger.info("Received request to edit customer id: '{}'", customerId);
        CustomerResponseDTO customerResponseDTO = customerUseCase.edit(customerId, customerUpdateDTO);
        logger.info("Customer id '{}' updated successfully", customerResponseDTO.id());
        return ResponseEntity.ok(customerResponseDTO);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "Delete a customer",
            description = "Delete a customer. Requires role USER or ADMIN. Publishes an event to delete the corresponding user in the authentication service.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Customer deleted successfully. No content is returned in the response body"
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
                    description = "Not Found — no customer exists with the provided ID",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error — an unexpected error occurred while communicating with Keycloak (e.g., creating, updating, or deleting a user, assigning roles, or validating tokens). Contact the development team if the issue persists.",
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
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long customerId) {
        logger.info("Received request to delete customer id: '{}'", customerId);
        customerUseCase.delete(customerId);
        logger.info("Customer id '{}' deleted successfully", customerId);
        return ResponseEntity.noContent().build();
    }
}
