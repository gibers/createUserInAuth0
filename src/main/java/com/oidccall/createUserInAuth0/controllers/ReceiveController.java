package com.oidccall.createUserInAuth0.controllers;

import com.oidccall.createUserInAuth0.config.ConfigMavariable;
import com.oidccall.createUserInAuth0.config.TokenFromAuth0;
import com.oidccall.createUserInAuth0.dtos.ResponseAuthApiV2UsersDto;
import com.oidccall.createUserInAuth0.dtos.ResponseAuthTokenDto;
import com.oidccall.createUserInAuth0.feignCalls.ApiV2UsersRequest;
import com.oidccall.createUserInAuth0.feignCalls.AuthTokenRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class ReceiveController {

  @NonNull
  final private ConfigMavariable configMavariable;
  @NonNull
  final private AuthTokenRequest authTokenRequest;
  @NonNull
  final private ApiV2UsersRequest apiV2UsersRequest;

  //ReceiveController_hello.md
  @GetMapping("/hello")
  public String hello() {
    return "hello 1 - " + configMavariable.getProfile();
  }

  // ReceiveController_token.md
  @GetMapping("/token")
  public ResponseAuthTokenDto token() {
    log.debug("Token: {}", getTokenFromAuth0ManagementApiTestApplication());
    return getTokenFromAuth0ManagementApiTestApplication();
  }

  // ReceiveController_createUser.md
  @PutMapping("/user")
  public ResponseAuthApiV2UsersDto createUser() {
    String accessToken = this.getTokenFromAuth0ManagementApiTestApplication().getAccess_token();
    ResponseAuthApiV2UsersDto userCreated = this.apiV2UsersRequest.createUserInAuth0(accessToken);
    log.debug("userCreated: {}", userCreated);
    return userCreated;
  }

  private ResponseAuthTokenDto getTokenFromAuth0ManagementApiTestApplication() {
    TokenFromAuth0 tokenProvider = TokenFromAuth0.getInstance(authTokenRequest);
    return tokenProvider.getFullToken();
  }

}
