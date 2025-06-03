package com.oidccall.createUserInAuth0.dtos;

import lombok.Data;

@Data
public class ResponseAuthTokenDto {

  private String access_token;
  private String scope;
  private String expires_in;
  private String token_type;

}
