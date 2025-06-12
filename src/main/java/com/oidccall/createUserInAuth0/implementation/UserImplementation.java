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

  /**
   * Deletes a user from the Auth0 system based on their user ID.
   * The process begins by verifying the user's existence in Auth0.
   * @param userId the unique identifier of the user to be deleted in Auth0
   */
  public void deleteUserInAuth0(String userId) {
    ResponseAuthApiV2UsersDto userApiV2Users = this.apiV2UsersRequest.getUserApiV2Users(userId);
    this.apiV2UsersRequest.deleteUserApiV2Users(userApiV2Users.getUserId());
  }

}
