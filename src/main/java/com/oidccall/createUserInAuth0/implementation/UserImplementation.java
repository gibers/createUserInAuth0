package com.oidccall.createUserInAuth0.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oidccall.createUserInAuth0.dtos.front.FrontUserToCreateDto;
import com.oidccall.createUserInAuth0.dtos.front.SimpleUserData;
import com.oidccall.createUserInAuth0.dtos.mappers.UsersEntityMapper;
import com.oidccall.createUserInAuth0.entities.Users;
import com.oidccall.createUserInAuth0.exceptions.ErrorsEnum;
import com.oidccall.createUserInAuth0.repository.UsersRepository;
import com.oidccall.dtos.feign.ParamsAuthApiV2UpdateVerifiedEmail;
import com.oidccall.dtos.feign.ParamsAuthApiV2UsersDto;
import com.oidccall.dtos.feign.ResponseAuthApiV2UsersDto;
import com.oidccall.feigncallslib.feignCalls.ApiV2UsersRequestLib;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserImplementation {

  private final ApiV2UsersRequestLib apiV2UsersRequest;
  private final ApiV2UsersRequestLib apiV2UsersRequestLib;
  private final UsersRepository usersRepository;
  private final UpsertUserFromAuth0ToLocalDBProcess upsertUserFromAuth0ToLocalDBProcess;
  private final UpdateUserInAuth0Process updateUserInAuth0Process;

  /**
   * Deletes a user from the Auth0 system based on their user ID.
   * 1. The process begins by verifying the user's existence in Auth0.
   * 2. Delete the user in auth0 by his userId (auth0|686e2adc8100047172......).
   * 3. pass the column users#deleted for that user to true
   * @param userId the unique identifier of the user to be deleted in Auth0
   */
  public void deleteUserInAuth0(String userId) {
    ResponseAuthApiV2UsersDto userApiV2Users = this.apiV2UsersRequestLib.getUserApiV2Users(userId);
    this.apiV2UsersRequest.deleteUserApiV2Users(userApiV2Users.getUserId());
    passColumnUsersDeletedToTrueOrThrow(userApiV2Users.getUserId());
  }

  public ResponseAuthApiV2UsersDto createUserInAuth0(FrontUserToCreateDto userFromFront) throws JsonProcessingException {
    var paramsAuthApiV2UsersDto = userFromFront.toParamsForCreation();
    paramsAuthApiV2UsersDto = generateNicknameForCreation(paramsAuthApiV2UsersDto);
    ResponseAuthApiV2UsersDto userFromAuth0 = this.apiV2UsersRequest.createUserInAuth0(paramsAuthApiV2UsersDto);
    Users users = UsersEntityMapper.mapToUsersEntity(userFromAuth0, userFromFront);
    usersRepository.save(users);
    log.debug("userFromAuth0: {}", users);
    return userFromAuth0;
  }

  public void upsertUserInLocalDBImplementation(String userId) {
    this.upsertUserFromAuth0ToLocalDBProcess.process(userId);
  }

  public void updateUserInAuth0Implementation(String userId, @Valid SimpleUserData simpleUserData) {
    this.updateUserInAuth0Process.process(userId, simpleUserData);
  }

  public void passEmailToUnVerified(String userId) {
    // 1. update auth0.
    this.apiV2UsersRequest.updateUsers(userId, new ParamsAuthApiV2UpdateVerifiedEmail(false));
    // 2. update local db.
    this.usersRepository.findByAuth0UserIdAndDeletedIsFalse(userId).ifPresentOrElse(x -> {
      x.setEmail_verified(false);
      x.setLast_modified_email_verified(Instant.now());
      this.usersRepository.save(x);
    }, () -> {
      String format = String.format(ErrorsEnum.E_1003.getOriginaErrorMessage(), userId);
      log.error(format);
      throw new EntityNotFoundException(format);
    });
  }

  private void passColumnUsersDeletedToTrueOrThrow(String userId) {
    Optional<Users> byAuth0UserId = this.usersRepository.findByAuth0UserId(userId);
    byAuth0UserId.ifPresentOrElse(users -> {
      users.setDeleted(true);
      this.usersRepository.save(users);
    }, () -> {
      String format = String.format(ErrorsEnum.E_1002.getOriginaErrorMessage(), userId);
      log.error(format);
      throw new EntityNotFoundException(format);
    });
  }

  private ParamsAuthApiV2UsersDto generateNicknameForCreation(ParamsAuthApiV2UsersDto paramsAuthApiV2UsersDto) {
    if (StringUtils.isNotBlank(paramsAuthApiV2UsersDto.nickname())) {
      return paramsAuthApiV2UsersDto;
    }
    return paramsAuthApiV2UsersDto.withNickname(composeNickname.apply(paramsAuthApiV2UsersDto));
  }

  private final BiFunction<String, Integer, String> truncatIt = (field, minSize) -> {
    int firstNameTruncated = Math.min(field.length(), minSize);
    return field.substring(0, 1).toUpperCase() + field.toLowerCase().substring(1, firstNameTruncated);
  };

  private final Function<ParamsAuthApiV2UsersDto, String> composeNickname =
    (firstName) -> truncatIt.apply(firstName.given_name(), 12) + "_" + truncatIt.apply(firstName.family_name(), 4) + "$";

}
