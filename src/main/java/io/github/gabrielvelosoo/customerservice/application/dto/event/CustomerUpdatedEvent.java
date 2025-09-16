package io.github.gabrielvelosoo.customerservice.application.dto.event;

public record CustomerUpdatedEvent(
        Long customerId,
        String name,
        String lastName
    ) {
}
