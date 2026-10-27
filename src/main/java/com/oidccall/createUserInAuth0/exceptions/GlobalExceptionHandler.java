package com.oidccall.createUserInAuth0.exceptions;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

@RestController
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
    );
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  // Invoked when method parameter validation errors occur
  @ExceptionHandler(HandlerMethodValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ApplicationErrorResponse> handlerMethodValidationException(HandlerMethodValidationException ex) {
    String validationMessage = getValidationMessageFromAnnotationParameter(ex);

    log.error("Validation failed: {}", validationMessage, ex);
    ApplicationErrorResponse error = new ApplicationErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getClass().getName(),
            validationMessage,
            LocalDateTime.now()
    );
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ApplicationErrorResponse> handleRuntimeException(RuntimeException ex) {
    String messageToDisplay = (ex.getMessage().startsWith("E_1")) ? ex.getMessage() : "error in System";
    log.error("Public error message: {}", messageToDisplay, ex);
    ApplicationErrorResponse error = new ApplicationErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.getClass().getName(),
            messageToDisplay,
            LocalDateTime.now()
    );
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(UnauthorizedUserAccessException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ResponseEntity<ApplicationErrorResponse> handleUnauthorizedUserAccess(UnauthorizedUserAccessException ex) {
    ApplicationErrorResponse error = new ApplicationErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            ex.getClass().getName(),
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

  @NotNull
  private static String getValidationMessageFromAnnotationParameter(HandlerMethodValidationException ex) {
    String validationMessage = ex.getParameterValidationResults().stream()
            .flatMap(result -> result.getResolvableErrors().stream())
            .map(MessageSourceResolvable::getDefaultMessage)
            .filter(Objects::nonNull)
            .collect(Collectors.joining("; "));
    if (validationMessage.isBlank()) {
      validationMessage = ex.getMessage();
    }
    return validationMessage;
  }

}
