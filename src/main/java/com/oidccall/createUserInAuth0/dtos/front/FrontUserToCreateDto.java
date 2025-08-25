package com.oidccall.createUserInAuth0.dtos.front;

import com.oidccall.createUserInAuth0.validation.PhoneNumber;
import com.oidccall.dtos.enums.GenderEnumDto;
import com.oidccall.dtos.feign.ParamsAuthApiV2UsersDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.With;

public record FrontUserToCreateDto(
  @NotBlank
  @Pattern(regexp = "(\\S)+@(\\w)+\\.[a-zA-Z]{2,10}", message = "poorly formatted email")
  String email,
  @With
  @NotBlank
  @PhoneNumber
  String phone_number,
  Object user_metadata,
  boolean blocked,
  boolean email_verified,
  boolean phone_verified,
  Object app_metadata,
  @With
  @NotBlank
  String given_name,
  @With
  @NotBlank
  String family_name,
  String name,
  @With
  String nickname,
  String picture,
  String user_id,
  String connection,
  @NotBlank
  String password,
  boolean verify_email,
  String username,
  GenderEnumDto gender
) {

  public ParamsAuthApiV2UsersDto toParamsForCreation() {
    return new ParamsAuthApiV2UsersDto(
      email().trim(),
      phone_number().replace(" ", ""),
      user_metadata(),
      false,
      false,
      false,
      app_metadata(),
      given_name().trim(),
      family_name().trim(),
      null,
      (nickname() == null) ? null : nickname().trim(),
      null,
      null,
      "Username-Password-Authentication",
      password().trim(),
      true,
      null
    );
  }

}
