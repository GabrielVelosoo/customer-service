package io.github.gabrielvelosoo.customerservice.infrastructure.exception;

public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
