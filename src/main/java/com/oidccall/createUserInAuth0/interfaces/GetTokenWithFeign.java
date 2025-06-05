package com.oidccall.createUserInAuth0.interfaces;

import com.oidccall.createUserInAuth0.config.FeignConfiguration;
import com.oidccall.createUserInAuth0.dtos.ParamsAuthApiV2UsersDto;
import com.oidccall.createUserInAuth0.dtos.ParamsAuthTokenDto;
import com.oidccall.createUserInAuth0.dtos.ResponseAuthApiV2UsersDto;
import com.oidccall.createUserInAuth0.dtos.ResponseAuthTokenDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.server.ResponseStatusException;

@FeignClient(
    name = "userClient",
    url = "${auth0.domain}",
    configuration = FeignConfiguration.class)
public interface GetTokenWithFeign {

  @PostMapping("/oauth/token")
  ResponseAuthTokenDto postOauthToken(@RequestBody ParamsAuthTokenDto paramsAuthTokenDto);

  @PostMapping("/api/v2/users")
  ResponseAuthApiV2UsersDto createUserApiV2Users(
      @RequestHeader("Authorization") String bearerToken,
      @RequestBody ParamsAuthApiV2UsersDto paramsAuthApiV2UsersDto);

  @GetMapping(value = "/api/v2/users/{userId}", consumes = "application/json")
  ResponseAuthApiV2UsersDto getUserApiV2Users(
      @RequestHeader("Authorization") String bearerToken,
      @PathVariable("userId") String userId
  ) throws ResponseStatusException;

}
