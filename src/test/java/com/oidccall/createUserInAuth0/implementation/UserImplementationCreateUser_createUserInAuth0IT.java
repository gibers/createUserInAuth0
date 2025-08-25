package com.oidccall.createUserInAuth0.implementation;

import com.oidccall.createUserInAuth0.dtos.front.FrontUserToCreateDto;
import com.oidccall.createUserInAuth0.entities.Users;
import com.oidccall.createUserInAuth0.repository.UsersRepository;
import com.oidccall.createUserInAuth0.utils.TestUtilsFunctions;
import com.oidccall.dtos.feign.ParamsAuthApiV2UsersDto;
import com.oidccall.dtos.feign.ResponseAuthApiV2UsersDto;
import com.oidccall.getadmintoken.feignCalls.ApiV2UsersRequestLib;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
public class UserImplementationCreateUser_createUserInAuth0IT {

  @Autowired
  private UserImplementation userImplementation;

  @Autowired
  private UsersRepository usersRepository;

  @MockitoBean
  private ApiV2UsersRequestLib apiV2UsersRequest;

  @Test
  @Transactional
  void whenCallingCreateUserInAuth0_WithAUserThatDoesNotExistInDB_insertionShouldPassed() throws IOException {
    // GIVEN:
    var frontUserToCreateDto = TestUtilsFunctions.getObjectFromResource("UserImplementation/createUserInAuth0/frontUser1.json", FrontUserToCreateDto.class);
    var responseAuthApiV2UsersDto = TestUtilsFunctions.getObjectFromResource("UserImplementation/createUserInAuth0/ResponseAuthApiV2UsersDto1.json", ResponseAuthApiV2UsersDto.class);
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
    var frontUserToCreateDto = TestUtilsFunctions.getObjectFromResource(
      "UserImplementation/createUserInAuth0/frontUser1.json", FrontUserToCreateDto.class);
    assert userInDBWithDeletedTrue.getEmail().equals(frontUserToCreateDto.email()) : "Les adresses email doivent être identiques";
    assert userInDBWithDeletedTrue.getPhone_number().equals(frontUserToCreateDto.phone_number()) : "Les phone_number doivent être identiques";
    var responseAuthApiV2UsersDto = TestUtilsFunctions.getObjectFromResource("UserImplementation/createUserInAuth0/ResponseAuthApiV2UsersDto1.json", ResponseAuthApiV2UsersDto.class);
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
    var frontUserToCreateDto = TestUtilsFunctions.getObjectFromResource(
      "UserImplementation/createUserInAuth0/frontUser1.json", FrontUserToCreateDto.class);
    assert userInDBWithDeletedTrue.getEmail().equals(frontUserToCreateDto.email()) : "Les adresses email doivent être identiques";
    assert userInDBWithDeletedTrue.getPhone_number().equals(frontUserToCreateDto.phone_number()) : "Les phone_number doivent être identiques";
    var responseAuthApiV2UsersDto = TestUtilsFunctions.getObjectFromResource("UserImplementation/createUserInAuth0/ResponseAuthApiV2UsersDto1.json", ResponseAuthApiV2UsersDto.class);
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
    var userWithDeletedTrue = TestUtilsFunctions.getObjectFromResource("UserImplementation/createUserInAuth0/userWithDeletedTrue1.json", Users.class);
    return this.usersRepository.save(userWithDeletedTrue);
  }

  private Users insertFakeTestUserInDBWithDeletedFalse() throws IOException {
    var userWithDeletedTrue = TestUtilsFunctions.getObjectFromResource("UserImplementation/createUserInAuth0/userWithDeletedTrue1.json", Users.class);
    userWithDeletedTrue.setDeleted(false);
    return this.usersRepository.save(userWithDeletedTrue);
  }

}
