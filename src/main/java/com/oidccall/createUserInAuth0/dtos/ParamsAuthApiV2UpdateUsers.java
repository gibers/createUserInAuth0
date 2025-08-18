package com.oidccall.createUserInAuth0.dtos;

import lombok.With;
import org.apache.commons.lang3.StringUtils;

public record ParamsAuthApiV2UpdateUsers(
  @With String email,
  @With Boolean email_verified,
  @With Boolean verify_email,

  @With String given_name,
  @With String family_name,
  @With String nickname,
  UserMetada user_metadata
) implements IParamsAuthApiV2UpdateUsers {

  public static ParamsAuthApiV2UpdateUsers forEmail(String email, ResponseAuthApiV2UsersDto responseAuthApiV2UsersDto) {
    return new ParamsAuthApiV2UpdateUsers(
      email.trim(), false,true
      , responseAuthApiV2UsersDto.getGiven_name(), responseAuthApiV2UsersDto.getFamily_name()
      , responseAuthApiV2UsersDto.getNickname()
      , responseAuthApiV2UsersDto.getUser_metadata());
  }

  public static ParamsAuthApiV2UpdateUsers forData(String family_name, String given_name, String nickname, UserMetada user_metadata
    , ResponseAuthApiV2UsersDto responseAuthApiV2UsersDto) {
    return new ParamsAuthApiV2UpdateUsers(
      responseAuthApiV2UsersDto.getEmail(), responseAuthApiV2UsersDto.isEmailVerified(),false
      , StringUtils.defaultIfBlank(given_name.trim(), null)
      , StringUtils.defaultIfBlank(family_name.trim(), null)
      , StringUtils.defaultIfBlank(nickname.trim(), null)
      , user_metadata);
  }

}
