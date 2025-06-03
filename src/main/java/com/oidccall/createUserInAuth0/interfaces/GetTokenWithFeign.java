package com.oidccall.createUserInAuth0.interfaces;
import com.oidccall.createUserInAuth0.dtos.ParamsAuthApiV2UsersDto;
import com.oidccall.createUserInAuth0.dtos.ParamsAuthTokenDto;
import com.oidccall.createUserInAuth0.dtos.ResponseAuthApiV2UsersDto;
import com.oidccall.createUserInAuth0.dtos.ResponseAuthTokenDto;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface GetTokenWithFeign {

  @RequestLine("POST /oauth/token")
  @Headers("Content-Type: application/json")
  ResponseAuthTokenDto postOauthToken(ParamsAuthTokenDto paramsAuthTokenDto);

  @RequestLine("POST /api/v2/users")
  @Headers({
      "Content-Type: application/json",
      "Authorization: Bearer {token}"
  })
  ResponseAuthApiV2UsersDto postApiV2Users(@Param("token") String token, ParamsAuthApiV2UsersDto paramsAuthApiV2UsersDto);

}
