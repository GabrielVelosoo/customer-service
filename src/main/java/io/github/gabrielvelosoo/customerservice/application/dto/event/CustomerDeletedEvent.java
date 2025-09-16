package io.github.gabrielvelosoo.customerservice.application.dto.event;

public record CustomerDeletedEvent(
        Long customerId
    ) {
}
