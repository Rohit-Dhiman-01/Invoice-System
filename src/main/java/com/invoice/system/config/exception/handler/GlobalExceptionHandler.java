package com.invoice.system.config.exception.handler;

import com.invoice.system.config.exception.CustomerNotFoundException;
import com.invoice.system.config.exception.QuoteNotFoundException;
import com.invoice.system.config.exception.VendorNotFoundException;
import com.invoice.system.config.exception.model.ErrorDetails;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler({
    VendorNotFoundException.class,
    CustomerNotFoundException.class,
    QuoteNotFoundException.class
  })
  public ResponseEntity<ErrorDetails> handleNotFound(RuntimeException ex) {
    return ResponseEntity.ok(new ErrorDetails(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, String>> handleConstraintViolation(
      ConstraintViolationException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getConstraintViolations()
        .forEach(
            cv -> {
              String path = cv.getPropertyPath().toString();
              String message = cv.getMessage();
              errors.put(path, message);
            });

    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }
}
