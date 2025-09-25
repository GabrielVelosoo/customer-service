package io.github.gabrielvelosoo.customerservice.application.dto.event;

public record CustomerCreatedEvent(
        Long customerId,
        String name,
        String lastName,
        String email,
        String password
    ) {
}
