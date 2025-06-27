package com.oidccall.createUserInAuth0.dtos.front;

import com.oidccall.createUserInAuth0.enums.GenderEnum;

public record FrontUserToCreateDto(
    String email,
    String phone_number,
    Object user_metadata,
    boolean blocked,
    boolean email_verified,
    boolean phone_verified,
    Object app_metadata,
    String given_name,
    String family_name,
    String name,
    String nickname,
    String picture,
    String user_id,
    String connection,
    String password,
    boolean verify_email,
    String username,
    GenderEnum gender
) {}