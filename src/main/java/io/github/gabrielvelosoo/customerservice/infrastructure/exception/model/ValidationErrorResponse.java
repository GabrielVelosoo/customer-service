package io.github.gabrielvelosoo.customerservice.infrastructure.exception.model;

import java.time.LocalDateTime;
import java.util.List;

public record ValidationErrorResponse(
        Integer status,
        LocalDateTime timestamp,
        List<FieldError> errors
    ) {
}
