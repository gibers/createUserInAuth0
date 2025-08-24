package com.oidccall.createUserInAuth0.feignCalls;

import com.oidccall.createUserInAuth0.config.Auth0Properties;
import com.oidccall.createUserInAuth0.dtos.ParamsAuthTokenDto;
import com.oidccall.createUserInAuth0.dtos.ResponseAuthTokenDto;
import com.oidccall.createUserInAuth0.interfaces.GetTokenWithFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthTokenRequest {

  private final GetTokenWithFeign getTokenWithFeign;
  private final Auth0Properties auth0Properties;

  public AuthTokenRequest(GetTokenWithFeign getTokenWithFeign, Auth0Properties auth0Properties) {
    this.getTokenWithFeign = getTokenWithFeign;
    this.auth0Properties = auth0Properties;
  }

  public ResponseAuthTokenDto requestToken() {
    ParamsAuthTokenDto paramsAuthTokenDto = new ParamsAuthTokenDto(
      auth0Properties.getAuth0ManagementApi().getClientId(),
      auth0Properties.getAuth0ManagementApi().getClientSecret(),
      auth0Properties.getAuth0ManagementApi().getAudience(),
      "client_credentials");
    return getTokenWithFeign.postOauthToken(paramsAuthTokenDto);
  }

}
