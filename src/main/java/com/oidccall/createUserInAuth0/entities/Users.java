package com.oidccall.createUserInAuth0.entities;


import com.oidccall.createUserInAuth0.enums.GenderEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class Users {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.NONE)
  private long id;

  @Column(name = "auth0_user_id", nullable = false, length = 35)
  private String auth0UserId;

  @CreatedDate
  @Column(nullable = false)
  private Instant created_at;

  @LastModifiedDate
  @Column(nullable = false)
  private Instant modified_at;

  @LastModifiedBy
  private String last_modified_by;

  @Column(length = 100)
  private String nickname;

  public String getNickname() {
    return (StringUtils.isBlank(nickname)) ? "" : nickname;
  }

  @Column(length = 100)
  private String given_name;

  public String getGiven_name() {
    return (StringUtils.isBlank(given_name)) ? "" : given_name;
  }

  @Column(length = 100)
  private String family_name;

  public String getFamily_name() {
    return (StringUtils.isBlank(family_name)) ? "" : family_name;
  }

  @Column(nullable = false)
  private boolean email_verified;

  @CreatedDate
  @Column(nullable = false)
  private Instant last_modified_email_verified;

  @Column(nullable = false)
  private String email;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(columnDefinition = "gender_type")
  private GenderEnum gender;

  private String picture;

  @Column(length = 50)
  private String phone_number;

  public String getPhone_number() {
    return (StringUtils.isBlank(phone_number)) ? "" : phone_number;
  }

  private boolean enabled = true;

  private boolean deleted = false;

}

