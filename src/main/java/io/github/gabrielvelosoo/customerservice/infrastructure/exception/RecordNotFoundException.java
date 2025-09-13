package io.github.gabrielvelosoo.customerservice.infrastructure.exception;

public class RecordNotFoundException extends RuntimeException {

    public RecordNotFoundException(String message) {
        super(message);
    }
}
