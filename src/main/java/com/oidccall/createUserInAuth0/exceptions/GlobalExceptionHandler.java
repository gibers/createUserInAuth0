package com.oidccall.createUserInAuth0.exceptions;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    ex.printStackTrace();
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
      errors.put(error.getField(), error.getDefaultMessage())
    );
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ApplicationErrorResponse> handleRuntimeException(RuntimeException ex) {
    ex.printStackTrace();
    ApplicationErrorResponse error = new ApplicationErrorResponse(
      HttpStatus.INTERNAL_SERVER_ERROR.value(),
      ex.getCause().getMessage(),
      ex.getMessage(),
      LocalDateTime.now()
    );
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
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
    ex.printStackTrace();
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
