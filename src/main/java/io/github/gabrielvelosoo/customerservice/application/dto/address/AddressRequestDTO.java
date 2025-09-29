package io.github.gabrielvelosoo.customerservice.application.dto.address;

import io.github.gabrielvelosoo.customerservice.application.validator.group.ValidateNotBlank;
import io.github.gabrielvelosoo.customerservice.application.validator.group.ValidateOthers;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressRequestDTO(

        @NotBlank(message = "Required field", groups = ValidateNotBlank.class)
        @Size(min = 2, max = 100, message = "The field must be between 2 and 100 characters", groups = ValidateOthers.class)
        String contactName,

        @NotBlank(message = "Required field", groups = ValidateNotBlank.class)
        @Size(min = 2, max = 100, message = "The field must be between 2 and 100 characters", groups = ValidateOthers.class)
        String contactLastName,

        @NotBlank(message = "Required field", groups = ValidateNotBlank.class)
        @Size(min = 11, max = 11, message = "The field must have 11 characters", groups = ValidateOthers.class)
        String contactPhone,

        @NotBlank(message = "Required field", groups = ValidateNotBlank.class)
        @Size(min = 2, max = 150, message = "The field must be between 2 and 150 characters", groups = ValidateOthers.class)
        String address,

        @NotBlank(message = "Required field", groups = ValidateNotBlank.class)
        @Size(max = 10, message = "The field must have a maximum of 10 characters", groups = ValidateOthers.class)
        String number,

        @NotBlank(message = "Required field", groups = ValidateNotBlank.class)
        @Size(min = 2, max = 100, message = "The field must be between 2 and 100 characters", groups = ValidateOthers.class)
        String neighborhood,

        @NotBlank(message = "Required field", groups = ValidateNotBlank.class)
        @Size(min = 2, max = 100, message = "The field must be between 2 and 100 characters", groups = ValidateOthers.class)
        String city,

        @NotBlank(message = "Required field", groups = ValidateNotBlank.class)
        @Size(min = 2, max = 100, message = "The field must be between 2 and 100 characters", groups = ValidateOthers.class)
        String state,

        @NotBlank(message = "Required field", groups = ValidateNotBlank.class)
        @Size(min = 8, max = 8, message = "The field must have 8 characters", groups = ValidateOthers.class)
        String cep,

        @Size(max = 50, message = "The field must have a maximum of 50 characters", groups = ValidateOthers.class)
        String complement
    ) {
}
