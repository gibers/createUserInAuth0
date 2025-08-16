package com.oidccall.createUserInAuth0.entities.dtos;

import com.oidccall.createUserInAuth0.entities.Users;
import com.oidccall.createUserInAuth0.enums.GenderEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Deprecated
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersDto {

  private Long id;

  @NotBlank(message = "Auth0 user ID is required")
  @Size(max = 35, message = "Auth0 user ID must not exceed 35 characters")
  private String auth0UserId;

  private Instant createdAt;

  private Instant modifiedAt;

  private String lastModifiedBy;

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  private String email;

  private GenderEnum gender;

  private String nickname;
  private String given_name;
  private String family_name;
  private boolean email_verified;
  private String picture;
  private String phone_number;

  public static UsersDto fromEntity(Users user) {
    return UsersDto.builder()
      .id(user.getId())
      .auth0UserId(user.getAuth0UserId())
      .createdAt(user.getCreated_at())
      .modifiedAt(user.getModified_at())
      .lastModifiedBy(user.getLast_modified_by())
      .nickname(user.getNickname())
      .family_name(user.getFamily_name())
      .given_name(user.getGiven_name())
      .email_verified(user.isEmail_verified())
      .email(user.getEmail())
      .gender(user.getGender())
      .picture(user.getPicture())
      .phone_number(user.getPhone_number())
      .build();
  }

  public Users toEntity() {
    Users user = new Users();
    user.setGiven_name(this.given_name);
    user.setEmail(this.email);
    user.setGender(this.gender);
    user.setPicture(this.picture);
    user.setPhone_number(this.phone_number);
    return user;
  }

}
