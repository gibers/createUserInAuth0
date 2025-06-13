package com.oidccall.createUserInAuth0.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.With;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ParamsAuthApiV2UsersDto(
    String email,
    @With String phone_number,
    Object user_metadata,
    boolean blocked,
    boolean email_verified,
    boolean phone_verified,
    Object app_metadata,
    @With String given_name,
    @With String family_name,
    @With String name,
    @With String nickname,
    @With String picture,
    String user_id,
    String connection,
    String password,
    boolean verify_email,
    @With String username
) {

  public ParamsAuthApiV2UsersDto(String email, String connection, String password) {
    this(email, null, null, false, false, false, null, null, null, null, null, null, null, connection, password, false, null);
  }

}
