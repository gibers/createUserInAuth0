package com.oidccall.createUserInAuth0.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = IsoDateValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsoDate {

  String message() default "Date must be a valid ISO date in yyyy-MM-dd format";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
