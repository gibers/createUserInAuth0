package com.oidccall.createUserInAuth0.validation;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsoDateValidator implements ConstraintValidator<IsoDate, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isBlank()) {
      return false;
    }

    try {
      LocalDate.parse(value);
      return true;
    } catch (DateTimeParseException exception) {
      return false;
    }
  }
}
