package com.oidccall.createUserInAuth0.dtos;

import jakarta.annotation.Nullable;
import lombok.With;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

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

  public static ParamsAuthApiV2UpdateUsers forData(@Nullable String family_name, @Nullable String given_name, @Nullable String nickname, UserMetada user_metadata
    , ResponseAuthApiV2UsersDto responseAuthApiV2UsersDto) {
    return new ParamsAuthApiV2UpdateUsers(
      responseAuthApiV2UsersDto.getEmail(), responseAuthApiV2UsersDto.isEmailVerified(),false
      , (StringUtils.defaultIfBlank(given_name, null) == null) ? null : Objects.requireNonNull(given_name).trim()
      , (StringUtils.defaultIfBlank(family_name, null) == null) ? null : Objects.requireNonNull(family_name).trim()
      , (StringUtils.defaultIfBlank(nickname, null) == null) ? null : Objects.requireNonNull(nickname).trim()
      , user_metadata);
  }

}
