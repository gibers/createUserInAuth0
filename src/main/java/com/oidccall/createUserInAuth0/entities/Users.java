package com.oidccall.createUserInAuth0.entities;


import com.oidccall.createUserInAuth0.enums.GenderEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Entity
@Data
public class Users {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.NONE)
  private long id;

  @Column(name = "auth0_user_id", nullable = false, length = 35)
  @Setter(AccessLevel.NONE)
  private String auth0UserId;

  @CreatedDate
  @Column(nullable = false)
  private Instant created_at;

  @LastModifiedDate
  @Column(nullable = false)
  private Instant modified_at;

  @LastModifiedBy
  private String last_modified_by;

  @Column(nullable = false, length = 200)
  private String username;

  @Column(unique = true, nullable = false)
  private String email;

  @Enumerated(EnumType.STRING)
  private GenderEnum gender;

  private String picture;

}

