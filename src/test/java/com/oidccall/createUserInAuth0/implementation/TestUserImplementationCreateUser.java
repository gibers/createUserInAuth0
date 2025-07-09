package com.oidccall.createUserInAuth0.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.oidccall.createUserInAuth0.dtos.ParamsAuthApiV2UsersDto;
import com.oidccall.createUserInAuth0.dtos.ResponseAuthApiV2UsersDto;
import com.oidccall.createUserInAuth0.dtos.front.FrontUserToCreateDto;
import com.oidccall.createUserInAuth0.entities.Users;
import com.oidccall.createUserInAuth0.feignCalls.ApiV2UsersRequest;
import com.oidccall.createUserInAuth0.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Slf4j
@ActiveProfiles("dev")
public class TestUserImplementationCreateUser {

  @Autowired
  private UserImplementation userImplementation;

  @Autowired
  private UsersRepository usersRepository;

  @MockBean
  private ApiV2UsersRequest apiV2UsersRequest;

  @Test
  @EnabledIfEnvironmentVariable(named = "spring.profiles.active", matches = "dev")
  @Transactional
  void whenCallingCreateUserInAuth0WithAUserThatDoesNotExistInDB() throws IOException {
    // GIVEN:
    FrontUserToCreateDto frontUserToCreateDto = getFrontUserToCreateDto();
    ResponseAuthApiV2UsersDto responseAuthApiV2UsersDto = getResponseAuthApiV2UsersDto();
    when(this.apiV2UsersRequest.createUserInAuth0(any(ParamsAuthApiV2UsersDto.class))).thenReturn(responseAuthApiV2UsersDto);

    // WHEN:
    this.userImplementation.createUserInAuth0(frontUserToCreateDto);

    // THEN:
    Optional<Users> byEmail = this.usersRepository.findByEmailAndDeletedIsFalse(responseAuthApiV2UsersDto.getEmail());
    Assertions.assertTrue(byEmail.isPresent());
    Assertions.assertEquals(responseAuthApiV2UsersDto.getUserId(), byEmail.get().getAuth0UserId());
  }

  @Test
  @EnabledIfEnvironmentVariable(named = "spring.profiles.active", matches = "dev")
  @Transactional
  void whenCallingCreateUserInAuth0WithAUserThatExistInDBWithDeletedTrue() throws IOException {
    // GIVEN:
    this.insertFakeTestUserInDBWithDeletedTrue();
    FrontUserToCreateDto frontUserToCreateDto = getFrontUserToCreateDto();
    ResponseAuthApiV2UsersDto responseAuthApiV2UsersDto = getResponseAuthApiV2UsersDto();
    when(this.apiV2UsersRequest.createUserInAuth0(any(ParamsAuthApiV2UsersDto.class))).thenReturn(responseAuthApiV2UsersDto);

    // WHEN:
    this.userImplementation.createUserInAuth0(frontUserToCreateDto);

    // THEN:
    Optional<Users> byEmail = this.usersRepository.findByEmailAndDeletedIsFalse(responseAuthApiV2UsersDto.getEmail());
    Assertions.assertTrue(byEmail.isPresent());
    Assertions.assertEquals(responseAuthApiV2UsersDto.getUserId(), byEmail.get().getAuth0UserId());
    List<Users> byEmail1 = this.usersRepository.findByEmail("fakeTestUser@mozmail.com");
    Assertions.assertEquals(2, byEmail1.size());
  }

  // todo: add a test that verifies that when a user with deleted false exists in DB, we can add another one with the same email.

  // -----------------------------------------------------------------------------------------------

  private void insertFakeTestUserInDBWithDeletedTrue() throws IOException {
    this.usersRepository.save(getUserWithDeletedTrue1());
  }

  private static ResponseAuthApiV2UsersDto getResponseAuthApiV2UsersDto() throws IOException {
    ClassPathResource resource1 = new ClassPathResource("ResponseAuthApiV2UsersDto/responseAuth1.json");
    return getObjectMapper().readValue(resource1.getFile(), ResponseAuthApiV2UsersDto.class);
  }

  private static FrontUserToCreateDto getFrontUserToCreateDto() throws IOException {
    ClassPathResource resource = new ClassPathResource("FrontUserToCreateDto/frontUser1.json");
    return getObjectMapper().readValue(resource.getFile(), FrontUserToCreateDto.class);
  }

  private static Users getUserWithDeletedTrue1() throws IOException {
    ClassPathResource resource = new ClassPathResource("Users/userWithDeletedTrue1.json");
    return getObjectMapper().readValue(resource.getFile(), Users.class);
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    return objectMapper;
  }

}
