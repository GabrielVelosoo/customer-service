package io.github.gabrielvelosoo.customerservice.application.dto.address;

public record AddressResponseDTO(
        Long id,
        String contactName,
        String contactLastName,
        String contactPhone,
        String address,
        String number,
        String neighborhood,
        String city,
        String state,
        String cep,
        String complement
    ) {
}
