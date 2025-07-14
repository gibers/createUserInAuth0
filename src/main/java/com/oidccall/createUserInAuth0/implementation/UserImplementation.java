package com.oidccall.createUserInAuth0.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oidccall.createUserInAuth0.dtos.ParamsAuthApiV2UsersDto;
import com.oidccall.createUserInAuth0.dtos.ResponseAuthApiV2UsersDto;
import com.oidccall.createUserInAuth0.dtos.front.FrontUserToCreateDto;
import com.oidccall.createUserInAuth0.dtos.mappers.UsersEntityMapper;
import com.oidccall.createUserInAuth0.entities.Users;
import com.oidccall.createUserInAuth0.exceptions.ErrorsEnum;
import com.oidccall.createUserInAuth0.feignCalls.ApiV2UsersRequest;
import com.oidccall.createUserInAuth0.repository.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserImplementation {

  private final ApiV2UsersRequest apiV2UsersRequest;
  private final UsersRepository usersRepository;

  /**
   * Deletes a user from the Auth0 system based on their user ID.
   * 1. The process begins by verifying the user's existence in Auth0.
   * 2. Delete the user in auth0 by his userId (auth0|686e2adc8100047172......).
   * 3. pass the column users#deleted for that user to true
   * @param userId the unique identifier of the user to be deleted in Auth0
   */
  public void deleteUserInAuth0(String userId) {
    ResponseAuthApiV2UsersDto userApiV2Users = this.apiV2UsersRequest.getUserApiV2Users(userId);
    this.apiV2UsersRequest.deleteUserApiV2Users(userApiV2Users.getUserId());
    passColumnUsersDeletedToTrueOrThrow(userApiV2Users);
  }

  public ResponseAuthApiV2UsersDto createUserInAuth0(FrontUserToCreateDto userFromFront) throws JsonProcessingException {
    ParamsAuthApiV2UsersDto paramsAuthApiV2UsersDto = replaceEmptyStringWithNull(userFromFront);
    ResponseAuthApiV2UsersDto userFromAuth0 = this.apiV2UsersRequest.createUserInAuth0(paramsAuthApiV2UsersDto);
    Users users = UsersEntityMapper.mapToUsersEntity(userFromAuth0, userFromFront);
    usersRepository.save(users);
    log.debug("userFromAuth0: {}", users);
    return userFromAuth0;
  }

  private void passColumnUsersDeletedToTrueOrThrow(ResponseAuthApiV2UsersDto userApiV2Users) {
    Optional<Users> byAuth0UserId = this.usersRepository.findByAuth0UserId(userApiV2Users.getUserId());
    byAuth0UserId.ifPresentOrElse(users -> {
      users.setDeleted(true);
      this.usersRepository.save(users);
    }, () -> {
      String format = String.format(ErrorsEnum.E_1002.getOriginaErrorMessage(), userApiV2Users.getUserId());
      log.error(format);
      throw new EntityNotFoundException(format);
    });
  }

  private ParamsAuthApiV2UsersDto replaceEmptyStringWithNull(FrontUserToCreateDto userToCreateDto) {
    ParamsAuthApiV2UsersDto paramsAuthApiV2UsersDto = ParamsAuthApiV2UsersDto.fromFrontDto(userToCreateDto);

    if (StringUtils.isBlank(paramsAuthApiV2UsersDto.name().trim())) {
      paramsAuthApiV2UsersDto = paramsAuthApiV2UsersDto.withName(null);
    }
    if (StringUtils.isBlank(paramsAuthApiV2UsersDto.username().trim())) {
      paramsAuthApiV2UsersDto = paramsAuthApiV2UsersDto.withUsername(null);
    }
    if (StringUtils.isBlank(paramsAuthApiV2UsersDto.picture().trim())) {
      paramsAuthApiV2UsersDto = paramsAuthApiV2UsersDto.withPicture(null);
    }
    if (StringUtils.isBlank(paramsAuthApiV2UsersDto.nickname().trim())) {
      paramsAuthApiV2UsersDto = paramsAuthApiV2UsersDto.withNickname(null);
    }
    if (StringUtils.isBlank(paramsAuthApiV2UsersDto.given_name().trim())) {
      paramsAuthApiV2UsersDto = paramsAuthApiV2UsersDto.withGiven_name(null);
    }
    if (StringUtils.isBlank(paramsAuthApiV2UsersDto.family_name().trim())) {
      paramsAuthApiV2UsersDto = paramsAuthApiV2UsersDto.withFamily_name(null);
    }
    if (StringUtils.isBlank(paramsAuthApiV2UsersDto.phone_number().trim())) {
      paramsAuthApiV2UsersDto = paramsAuthApiV2UsersDto.withPhone_number(null);
    }
    return paramsAuthApiV2UsersDto;
  }

}
