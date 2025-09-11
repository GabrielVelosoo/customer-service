package io.github.gabrielvelosoo.customerservice.infrastructure.exception.handler;

import io.github.gabrielvelosoo.customerservice.infrastructure.exception.model.FieldError;
import io.github.gabrielvelosoo.customerservice.infrastructure.exception.model.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        int status = HttpStatus.UNPROCESSABLE_ENTITY.value();
        LocalDateTime timestamp = LocalDateTime.now();
        List<FieldError> errors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> new FieldError(error.getField(), error.getDefaultMessage()))
                .toList();
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(status, timestamp, errors);
        return ResponseEntity.status(status).body(errorResponse);
    }
}
