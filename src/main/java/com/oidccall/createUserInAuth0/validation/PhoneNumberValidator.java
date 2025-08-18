package com.oidccall.createUserInAuth0.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

  @Override
  public void initialize(PhoneNumber constraintAnnotation) {}

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (StringUtils.isBlank(value)) {
      return true; // Laissez @NotNull/@NotBlank gérer les valeurs null
    }

    String withoutSpace = value.replaceAll("\\s", "");
    return withoutSpace.matches("^\\+\\d{11,15}$");
  }

}
