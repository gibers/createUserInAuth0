package com.oidccall.createUserInAuth0.controllers;

import com.oidccall.dtos.feign.ResponseAuthTokenDto;
import com.oidccall.feigncallslib.Auth0Properties;
import com.oidccall.feigncallslib.SingletonAdminToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class ReceiveController {

  @NonNull
  final private Auth0Properties auth0Properties;
  @NonNull
  private final SingletonAdminToken singletonAdminToken;

  //ReceiveController_hello.md
  @GetMapping("/hello")
  public String hello() {
    return "hello 1 - " + auth0Properties.getFunctionalUser().getClientId();
  }

  // ReceiveController_token.md
  @GetMapping("/token")
  public ResponseAuthTokenDto token() {
    return singletonAdminToken.getFullToken();
  }

}
