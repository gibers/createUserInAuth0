package com.oidccall.createUserInAuth0.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oidccall.createUserInAuth0.dtos.front.FrontUserToCreateDto;
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
    @With String name, // we don't use it, we use the family_name
    @With String nickname,
    @With String picture,
    String user_id,
    String connection,
    String password,
    boolean verify_email,
    @With String username
) {

//  public ParamsAuthApiV2UsersDto(String email, String connection, String password) {
//    this(email, null, null, false, false, false, null, null, null, null, null, null, null, connection, password, false, null);
//  }

  public static ParamsAuthApiV2UsersDto convertFrontDtoForCreation(FrontUserToCreateDto frontDto) {
    return new ParamsAuthApiV2UsersDto(
        frontDto.email().trim(),
        frontDto.phone_number().replace(" ", ""),
        frontDto.user_metadata(),
        false,
        false,
        false,
        frontDto.app_metadata(),
        frontDto.given_name().trim(),
        frontDto.family_name().trim(),
        null,
        (frontDto.nickname() == null)? null: frontDto.nickname().trim(),
        null,
        null,
        "Username-Password-Authentication",
        frontDto.password().trim(),
        true,
        null
    );
  }

}
