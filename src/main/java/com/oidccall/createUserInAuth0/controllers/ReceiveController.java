package com.oidccall.createUserInAuth0.controllers;

import com.oidccall.createUserInAuth0.config.ConfigMavariable;
import com.oidccall.dtos.feign.ResponseAuthTokenDto;
import com.oidccall.feigncallslib.TokenFromAuth0;
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
  final private ConfigMavariable configMavariable;
  @NonNull
  private final TokenFromAuth0 tokenFromAuth0;

  //ReceiveController_hello.md
  @GetMapping("/hello")
  public String hello() {
    return "hello 1 - " + configMavariable.getProfile();
  }

  // ReceiveController_token.md
  @GetMapping("/token")
  public ResponseAuthTokenDto token() {
    return tokenFromAuth0.getFullToken();
  }

}
