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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Slf4j
public class UserImplementationCreateUser_createUserInAuth0Test {

  @Autowired
  private UserImplementation userImplementation;

  @Autowired
  private UsersRepository usersRepository;

  @MockitoBean
  private ApiV2UsersRequest apiV2UsersRequest;

  @Test
  @Transactional
  void whenCallingCreateUserInAuth0_WithAUserThatDoesNotExistInDB_insertionShouldPassed() throws IOException {
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
  @Transactional
  void whenCallingCreateUserInAuth0_WithAUserInDBWithDeletedTrue_insertionShouldPassed() throws IOException {
    // GIVEN:
    Users userInDBWithDeletedTrue = this.insertFakeTestUserInDBWithDeletedTrue();
    FrontUserToCreateDto frontUserToCreateDto = getFrontUserToCreateDto();
    assert userInDBWithDeletedTrue.getEmail().equals(frontUserToCreateDto.email()) : "Les adresses email doivent être identiques";
    assert userInDBWithDeletedTrue.getPhone_number().equals(frontUserToCreateDto.phone_number()) : "Les phone_number doivent être identiques";
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

  @Test
  @Transactional
  void whenCallingCreateUserInAuth0_WithAUserInDBWithDeletedFalse_insertionShouldFailed() throws IOException {
    // GIVEN:
    Users userInDBWithDeletedTrue = this.insertFakeTestUserInDBWithDeletedFalse();
    FrontUserToCreateDto frontUserToCreateDto = getFrontUserToCreateDto();
    assert userInDBWithDeletedTrue.getEmail().equals(frontUserToCreateDto.email()) : "Les adresses email doivent être identiques";
    assert userInDBWithDeletedTrue.getPhone_number().equals(frontUserToCreateDto.phone_number()) : "Les phone_number doivent être identiques";
    ResponseAuthApiV2UsersDto responseAuthApiV2UsersDto = getResponseAuthApiV2UsersDto();
    when(this.apiV2UsersRequest.createUserInAuth0(any(ParamsAuthApiV2UsersDto.class))).thenReturn(responseAuthApiV2UsersDto);

    // WHEN:
    DataIntegrityViolationException exception = Assertions.assertThrows(
      DataIntegrityViolationException.class,
      () -> this.userImplementation.createUserInAuth0(frontUserToCreateDto)
    );
    // THEN
    Assertions.assertTrue(exception.getMessage().contains("duplicate key value violates unique constraint \"uk_users_email_enabled\""));
  }

  // -----------------------------------------------------------------------------------------------

  private Users insertFakeTestUserInDBWithDeletedTrue() throws IOException {
    return this.usersRepository.save(getUserWithDeletedTrue());
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

  private static FrontUserToCreateDto getFrontUserToCreateDto() throws IOException {
    ClassPathResource resource = new ClassPathResource("UserImplementationCreateUserTest/createUserInAuth0/frontUser1.json");
    return getObjectMapper().readValue(resource.getFile(), FrontUserToCreateDto.class);
  }

  private static Users getUserWithDeletedTrue() throws IOException {
    ClassPathResource resource = new ClassPathResource("UserImplementationCreateUserTest/createUserInAuth0/userWithDeletedTrue1.json");
    return getObjectMapper().readValue(resource.getFile(), Users.class);
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    return objectMapper;
  }

}
