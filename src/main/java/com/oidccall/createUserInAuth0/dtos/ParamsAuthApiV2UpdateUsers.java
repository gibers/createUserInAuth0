package com.oidccall.createUserInAuth0.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.With;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ParamsAuthApiV2UpdateUsers(
  @With String email,
  @With boolean email_verified,
  @With boolean verify_email,

  @With String phone_number,

  @With String given_name,
  @With String family_name,
  @With String nickname,
  UserMetada user_metadata
) {

  public ParamsAuthApiV2UpdateUsers(String email) {
    this(email, false, true, null, null, null, null, null);
  }

  public static ParamsAuthApiV2UpdateUsers forPhoneNumber(String phone_number) {
    return new ParamsAuthApiV2UpdateUsers(
      null,false,false, phone_number,null,null, null, null);
  }

  public static ParamsAuthApiV2UpdateUsers forData(String family_name, String given_name, String nickname, UserMetada user_metadata) {
    return new ParamsAuthApiV2UpdateUsers(
      null,false,false, null,given_name,family_name, nickname, user_metadata);
  }

}
