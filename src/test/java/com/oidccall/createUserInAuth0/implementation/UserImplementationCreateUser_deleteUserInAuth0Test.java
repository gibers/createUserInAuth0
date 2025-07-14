package com.oidccall.createUserInAuth0.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.oidccall.createUserInAuth0.dtos.ResponseAuthApiV2UsersDto;
import com.oidccall.createUserInAuth0.entities.Users;
import com.oidccall.createUserInAuth0.exceptions.ErrorsEnum;
import com.oidccall.createUserInAuth0.feignCalls.ApiV2UsersRequest;
import com.oidccall.createUserInAuth0.repository.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
@Slf4j
public class UserImplementationCreateUser_deleteUserInAuth0Test {

  @Autowired
  private UserImplementation userImplementation;

  @Autowired
  private UsersRepository usersRepository;

  @MockitoBean
  private ApiV2UsersRequest apiV2UsersRequest;

  @Test
  @Transactional
  void whenCallingDeleteUserInAuth0_WithAUserNonDeletedUser_heShouldPassToDeleted() throws IOException {
    // GIVEN:
    Users fakeUsers = insertFakeTestUserInDBWithDeletedFalse();
    ResponseAuthApiV2UsersDto responseAuthApiV2UsersDto = getResponseAuthApiV2UsersDto();
    when(this.apiV2UsersRequest.getUserApiV2Users(fakeUsers.getAuth0UserId())).thenReturn(responseAuthApiV2UsersDto);

    // WHEN:
    checkIfUserIsNotDeleted(fakeUsers);
    this.userImplementation.deleteUserInAuth0(fakeUsers.getAuth0UserId());

    // THEN:
    Optional<Users> byAuth0UserId = this.usersRepository.findByAuth0UserId(fakeUsers.getAuth0UserId());
    Assertions.assertTrue(byAuth0UserId.isPresent());
    Assertions.assertTrue(byAuth0UserId.get().isDeleted());
  }

  @Test
  @Transactional
  void whenCallingDeleteUserInAuth0_OnAnAbsentUser_methodShouldThrowError() throws IOException {
    // GIVEN:
    Users fakeUsers = getUserWithDeletedTrue();
    ResponseAuthApiV2UsersDto responseAuthApiV2UsersDto = getResponseAuthApiV2UsersDto();
    when(this.apiV2UsersRequest.getUserApiV2Users(fakeUsers.getAuth0UserId())).thenReturn(responseAuthApiV2UsersDto);

    // WHEN:
    checkIfUserIsAbsent(fakeUsers);
    EntityNotFoundException entityNotFoundException = Assertions.assertThrows(EntityNotFoundException.class,
      () -> this.userImplementation.deleteUserInAuth0(fakeUsers.getAuth0UserId()));

    // THEN:
    String format = String.format(ErrorsEnum.E_1002.getOriginaErrorMessage(), fakeUsers.getAuth0UserId());
    Assertions.assertTrue(entityNotFoundException.getMessage().contains(format));
  }

  // -----------------------------------------------------------------------------------------------

  private void checkIfUserIsNotDeleted(Users fakeUsers) {
    this.usersRepository.findByAuth0UserId(fakeUsers.getAuth0UserId()).ifPresentOrElse(users -> {
      assert !users.isDeleted() : "user should not be deleted";
    }, () -> {
      throw new RuntimeException("User not found: " + fakeUsers.getAuth0UserId());
    });
  }

  private void checkIfUserIsAbsent(Users fakeUsers) {
    this.usersRepository.findByAuth0UserId(fakeUsers.getAuth0UserId()).ifPresent(users -> {
      throw new RuntimeException("User should not be present: " + fakeUsers.getAuth0UserId());
    });
  }

  private Users insertFakeTestUserInDBWithDeletedFalse() throws IOException {
    Users userWithDeletedTrue = getUserWithDeletedTrue();
    userWithDeletedTrue.setDeleted(false);
    return this.usersRepository.save(userWithDeletedTrue);
  }

  private static ResponseAuthApiV2UsersDto getResponseAuthApiV2UsersDto() throws IOException {
    ClassPathResource resource1 = new ClassPathResource("UserImplementationCreateUserTest/createUserInAuth0/responseAuth1.json");
    return getObjectMapper().readValue(resource1.getFile(), ResponseAuthApiV2UsersDto.class);
  }

  private static Users getUserWithDeletedTrue() throws IOException {
    ClassPathResource resource = new ClassPathResource("UserImplementationCreateUserTest/deleteUserInAuth0/userWithDeletedFalse1.json");
    return getObjectMapper().readValue(resource.getFile(), Users.class);
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    return objectMapper;
  }

}
