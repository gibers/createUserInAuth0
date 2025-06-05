package com.oidccall.createUserInAuth0.controllers;

import com.oidccall.createUserInAuth0.dtos.ResponseAuthApiV2UsersDto;
import com.oidccall.createUserInAuth0.exceptions.UnauthorizedUserAccessException;
import com.oidccall.createUserInAuth0.feignCalls.ApiV2UsersRequest;
import com.oidccall.createUserInAuth0.implementation.UserImplementation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {

  @NonNull
  private final UserImplementation userImplementation;
  @NonNull
  final private ApiV2UsersRequest apiV2UsersRequest;

  // ReceiveController_createUser.md
  @PutMapping("/create")
  public ResponseAuthApiV2UsersDto createUser() {
    ResponseAuthApiV2UsersDto userCreated = this.apiV2UsersRequest.createUserInAuth0();
    log.debug("userCreated: {}", userCreated);
    return userCreated;
  }

  @GetMapping("/{userId}")
  public ResponseAuthApiV2UsersDto getUser(Authentication authentication, @PathVariable String userId)
      throws ResponseStatusException {
    if (!userId.equals(authentication.getName())) {
      throw new UnauthorizedUserAccessException("User " + userId + " does not correspond to the authorized user ");
    }
    return this.apiV2UsersRequest.getUserApiV2Users(userId);
  }

//  @GetMapping("/tt/{userIdT}")
//  public ResponseAuthApiV2UsersDto getUserT(Authentication authentication, @PathVariable String userId) {
//    if (!userId.equals(authentication.getName())) {
//      throw new UnauthorizedUserAccessException("User " + userId + " does not correspond to the authorized user ");
//    }
//    return this.userImplementation.getUserFromAuth0(userId);
//  }

  @DeleteMapping("/delete")
  public void deleteUser(Authentication authentication) {
    log.debug("User deleted: {}", authentication);
    String name = authentication.getName();
  }

//  private ResponseAuthTokenDto getTokenFromAuth0ManagementApiTestApplication() {
//    TokenFromAuth0Singleton tokenProvider = TokenFromAuth0Singleton.getInstance(authTokenRequest);
//    return tokenProvider.getFullToken();
//  }

}
