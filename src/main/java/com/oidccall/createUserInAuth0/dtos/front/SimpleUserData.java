package com.oidccall.createUserInAuth0.dtos.front;

import com.oidccall.createUserInAuth0.enums.GenderEnum;
import com.oidccall.createUserInAuth0.validation.PhoneNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.With;

public record SimpleUserData(
  @NotBlank
  @Pattern(regexp = "(\\S)+@(\\w)+\\.[a-zA-Z]{2,10}", message = "poorly formatted email")
  String email,

  @With
  String nickname,

  @With
  String given_name,

  @With
  String family_name,

  @PhoneNumber
  @With
  String phone_number,

  GenderEnum gender

) {}
