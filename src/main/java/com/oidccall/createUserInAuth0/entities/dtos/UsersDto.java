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
    
    @NotBlank(message = "Username is required")
    @Size(max = 200, message = "Username must not exceed 200 characters")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    private GenderEnum gender;
    
    private String picture;
    private String phone_number;
    
    public static UsersDto fromEntity(Users user) {
        return UsersDto.builder()
                .id(user.getId())
                .auth0UserId(user.getAuth0UserId())
                .createdAt(user.getCreated_at())
                .modifiedAt(user.getModified_at())
                .lastModifiedBy(user.getLast_modified_by())
                .username(user.getUsername())
                .email(user.getEmail())
                .gender(user.getGender())
                .picture(user.getPicture())
                .phone_number(user.getPhone_number())
                .build();
    }

    public Users toEntity() {
        Users user = new Users();
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setGender(this.gender);
        user.setPicture(this.picture);
        user.setPhone_number(this.phone_number);
        return user;
    }

}
