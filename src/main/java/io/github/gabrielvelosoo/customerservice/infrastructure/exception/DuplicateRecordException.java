package io.github.gabrielvelosoo.customerservice.infrastructure.exception;

public class DuplicateRecordException extends RuntimeException {

    public DuplicateRecordException(String message) {
        super(message);
    }
}
