package com.oidccall.createUserInAuth0.validation;

import com.oidccall.createUserInAuth0.dtos.front.FrontUserToCreateDto;
import com.oidccall.createUserInAuth0.utils.TestUtilsFunctions;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.metadata.ConstraintDescriptor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@Slf4j
public class PhoneNumberValidationTest {

  private static final FrontUserToCreateDto frontUserToCreateDto = TestUtilsFunctions.getObjectFromResource(
    "Validation/frontUser1.json", FrontUserToCreateDto.class);

  @Test
  void whenNoError_thenNoViolation() {
    // WHEN:
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    Set<ConstraintViolation<FrontUserToCreateDto>> violations = validator.validate(frontUserToCreateDto);

    // THEN:
    Assertions.assertEquals(0, violations.size());
  }

  @ParameterizedTest
  @MethodSource
  void testValidationPhoneNumber(String phoneNumber) throws NoSuchFieldException {
    // WHEN:
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    Set<ConstraintViolation<FrontUserToCreateDto>> violations = validator.validate(frontUserToCreateDto.withPhone_number(phoneNumber));

    // THEN:
    Assertions.assertEquals(1, violations.size());
    ConstraintDescriptor<?> constraintDescriptor = violations.iterator().next().getConstraintDescriptor();
    Class<? extends Annotation> validationThatFailed = constraintDescriptor.getAnnotation().annotationType();
    Assertions.assertEquals(getTypeOfValidationPhoneNumber(), validationThatFailed);
  }

  private static Stream<Arguments> testValidationPhoneNumber() {
    return Stream.of(
      Arguments.of("006656520102"),
      Arguments.of("+3665652011"),
      Arguments.of("-00665652010"),
      Arguments.of("+0665652010111122")
    );
  }

  // ---------------------------------------------------------------

  private static Class<? extends Annotation> getTypeOfValidationPhoneNumber() throws NoSuchFieldException {
    Field phoneNumberField = FrontUserToCreateDto.class.getDeclaredField("phone_number");
    return phoneNumberField.getAnnotation(PhoneNumber.class).annotationType();
  }

}
