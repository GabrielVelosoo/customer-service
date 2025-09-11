package io.github.gabrielvelosoo.customerservice.infrastructure.exception.model;

public record FieldError(
        String field,
        String message
    ) {
}
