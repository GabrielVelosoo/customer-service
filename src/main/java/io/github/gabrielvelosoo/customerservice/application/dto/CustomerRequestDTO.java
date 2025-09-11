package io.github.gabrielvelosoo.customerservice.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public record CustomerRequestDTO(

        @NotBlank(message = "Required field")
        @Size(min = 2, max = 100, message = "The field must be between 2 and 100 characters")
        String name,

        @NotBlank(message = "Required field")
        @Size(min = 2, max = 100, message = "The field must be between 2 and 100 characters")
        String lastName,

        @Email(message = "Invalid address e-mail")
        @NotBlank(message = "Required field")
        String email,

        @NotBlank(message = "Required field")
        String password,

        @CPF(message = "Invalid CPF")
        @NotBlank(message = "Required field")
        String cpf,

        @NotBlank(message = "Required field")
        String cep,

        @NotNull(message = "Required field")
        LocalDate birthDate
    ) {
}
