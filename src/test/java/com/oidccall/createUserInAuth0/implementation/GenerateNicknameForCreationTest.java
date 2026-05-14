package com.oidccall.createUserInAuth0.implementation;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.oidccall.createUserInAuth0.dtos.front.FrontUserToCreateDto;
import com.oidccall.createUserInAuth0.utils.TestUtilsFunctions;
import com.oidccall.dtos.feign.ParamsAuthApiV2UsersDto;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@Slf4j
public class GenerateNicknameForCreationTest {

  private final UserImplementation userImplementation = new UserImplementation(null,null, null, null, null, null);
  private static final FrontUserToCreateDto frontUserToCreateDto = TestUtilsFunctions.getObjectFromResource(
    "UserImplementation/generateNicknameForCreation/frontUser1.json", FrontUserToCreateDto.class);

  @ParameterizedTest
  @MethodSource
  void testGenerateNicknameForCreation(FrontUserToCreateDto frontUserToCreateDto1, String expectedNickname) {
    // GIVEN:
    ParamsAuthApiV2UsersDto paramsAuthApiV2UsersDto = frontUserToCreateDto1.toParamsForCreation();
    // WHEN:
    var result = (ParamsAuthApiV2UsersDto) ReflectionTestUtils.invokeMethod(userImplementation, "generateNicknameForCreation", paramsAuthApiV2UsersDto);
    // THEN:
    log.info("result: {}", expectedNickname);
    Assertions.assertEquals(expectedNickname, result.nickname());
  }

  private static Stream<Arguments> testGenerateNicknameForCreation() {
    return Stream.of(
      Arguments.of(GenerateNicknameForCreationTest.frontUserToCreateDto.withGiven_name("john").withFamily_name("doe").withNickname(null), "John_Doe$"),
      Arguments.of(GenerateNicknameForCreationTest.frontUserToCreateDto.withGiven_name("j").withFamily_name("d").withNickname(null), "J_D$"),
      Arguments.of(GenerateNicknameForCreationTest.frontUserToCreateDto.withGiven_name("johnGartnerMoreLetter").withFamily_name("DVeryLongName").withNickname(null), "Johngartnerm_Dver$"),
      Arguments.of(GenerateNicknameForCreationTest.frontUserToCreateDto.withGiven_name("johnGartnerMoreLetter").withFamily_name("DVeryLongName").withNickname("myNickNamePerso"), "myNickNamePerso")
    );
  }

}
