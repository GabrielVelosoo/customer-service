package io.github.gabrielvelosoo.customerservice.infrastructure.exception;

public class KeycloakException extends RuntimeException {

    public KeycloakException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeycloakException(String message) {
        super(message);
    }
}
