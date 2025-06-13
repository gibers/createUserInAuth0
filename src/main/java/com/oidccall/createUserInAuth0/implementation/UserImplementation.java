package com.oidccall.createUserInAuth0.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oidccall.createUserInAuth0.dtos.ParamsAuthApiV2UsersDto;
import com.oidccall.createUserInAuth0.dtos.ResponseAuthApiV2UsersDto;
import com.oidccall.createUserInAuth0.dtos.front.FrontUserToCreateDto;
import com.oidccall.createUserInAuth0.feignCalls.ApiV2UsersRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserImplementation {

  private final ApiV2UsersRequest apiV2UsersRequest;

  /**
   * Deletes a user from the Auth0 system based on their user ID.
   * The process begins by verifying the user's existence in Auth0.
   * @param userId the unique identifier of the user to be deleted in Auth0
   */
  public void deleteUserInAuth0(String userId) {
    ResponseAuthApiV2UsersDto userApiV2Users = this.apiV2UsersRequest.getUserApiV2Users(userId);
    this.apiV2UsersRequest.deleteUserApiV2Users(userApiV2Users.getUserId());
  }

  public ResponseAuthApiV2UsersDto createUserInAuth0(FrontUserToCreateDto userToCreateDto) {
    ObjectMapper objectMapper = new ObjectMapper();
    ParamsAuthApiV2UsersDto paramsAuthApiV2UsersDto = objectMapper.convertValue(userToCreateDto, ParamsAuthApiV2UsersDto.class);
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
    return this.apiV2UsersRequest.createUserInAuth0(paramsAuthApiV2UsersDto);
  }

}
