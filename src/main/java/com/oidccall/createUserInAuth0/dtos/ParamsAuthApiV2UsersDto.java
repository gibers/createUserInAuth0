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
    @With String name,
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

  public static ParamsAuthApiV2UsersDto fromFrontDto(FrontUserToCreateDto frontDto) {
    return new ParamsAuthApiV2UsersDto(
        frontDto.email(),
        frontDto.phone_number(),
        frontDto.user_metadata(),
        frontDto.blocked(),
        frontDto.email_verified(),
        frontDto.phone_verified(),
        frontDto.app_metadata(),
        frontDto.given_name(),
        frontDto.family_name(),
        frontDto.name(),
        frontDto.nickname(),
        frontDto.picture(),
        frontDto.user_id(),
        frontDto.connection(),
        frontDto.password(),
        frontDto.verify_email(),
        frontDto.username()
    );
  }

}
