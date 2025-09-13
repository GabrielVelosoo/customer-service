package io.github.gabrielvelosoo.customerservice.application.dto;

import io.github.gabrielvelosoo.customerservice.application.validator.group.ValidateNotBlank;
import io.github.gabrielvelosoo.customerservice.application.validator.group.ValidateOthers;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public record CustomerUpdateDTO(

        @NotBlank(message = "Required field", groups = ValidateNotBlank.class)
        @Size(min = 2, max = 100, message = "The field must be between 2 and 100 characters", groups = ValidateOthers.class)
        String name,

        @NotBlank(message = "Required field", groups = ValidateNotBlank.class)
        @Size(min = 2, max = 100, message = "The field must be between 2 and 100 characters", groups = ValidateOthers.class)
        String lastName,

        @CPF(message = "Invalid CPF", groups = ValidateOthers.class)
        @NotBlank(message = "Required field", groups = ValidateNotBlank.class)
        String cpf,

        @NotNull(message = "Required field", groups = ValidateNotBlank.class)
        LocalDate birthDate
    ) {
}
