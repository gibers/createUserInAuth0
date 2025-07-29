package com.oidccall.createUserInAuth0.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
          errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApplicationErrorResponse> handleRuntimeException(RuntimeException ex) {
        ApplicationErrorResponse error = new ApplicationErrorResponse(
          HttpStatus.INTERNAL_SERVER_ERROR.value(),
          "Internal Server Error",
          ex.getMessage(),
          LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UnauthorizedUserAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ApplicationErrorResponse> handleUnauthorizedUserAccess(UnauthorizedUserAccessException ex) {
        ApplicationErrorResponse error = new ApplicationErrorResponse(
          HttpStatus.FORBIDDEN.value(),
          "Access Denied",
          ex.getMessage(),
          LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApplicationErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        int statusCode = ex.getStatusCode().value();
        ApplicationErrorResponse error = new ApplicationErrorResponse(
          statusCode,
          ex.getReason(),
          ex.getMessage(),
          LocalDateTime.now()
        );
        return new ResponseEntity<>(error, ex.getStatusCode());
    }

}
