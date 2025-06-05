package com.oidccall.createUserInAuth0.implementation;

import com.oidccall.createUserInAuth0.dtos.ResponseAuthApiV2UsersDto;
import com.oidccall.createUserInAuth0.feignCalls.ApiV2UsersRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserImplementation {

  private final ApiV2UsersRequest apiV2UsersRequest;

  public ResponseAuthApiV2UsersDto getUserFromAuth0(String userId) {
    ResponseAuthApiV2UsersDto userApiV2Users = this.apiV2UsersRequest.getUserApiV2Users(userId);
    log.debug("userApiV2Users: {}", userApiV2Users);
    return userApiV2Users;
  }


}
