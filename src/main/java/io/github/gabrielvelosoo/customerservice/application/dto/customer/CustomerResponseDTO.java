package io.github.gabrielvelosoo.customerservice.application.dto.customer;

import java.time.LocalDate;

public record CustomerResponseDTO(
        Long id,
        String name,
        String lastName,
        String email,
        String cpf,
        String cep,
        LocalDate birthDate
    ) {
}
