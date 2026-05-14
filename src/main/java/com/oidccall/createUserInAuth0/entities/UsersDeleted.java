package com.oidccall.createUserInAuth0.entities;

import java.time.Instant;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.oidccall.dtos.enums.EmailStatusEnum;
import com.oidccall.dtos.enums.GenderEnumDto;

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

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class UsersDeleted {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.NONE)
  private long id;

  @Column(name = "auth0_user_id", nullable = false, length = 35)
  private String auth0UserId;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant modified_at;

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

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(name = "email_status")
  private EmailStatusEnum emailStatus = EmailStatusEnum.NEVER_VERIFIED;

  @Column(nullable = false)
  private Instant lastModifiedEmailVerified;

  @Column(nullable = false)
  private String email;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(columnDefinition = "gender_type")
  private GenderEnumDto gender;

  private String picture;

  @Column(length = 50)
  private String phone_number;

  private long jobExecutionId;

  private String stepName;

  public String getPhone_number() {
    return (StringUtils.isBlank(phone_number)) ? "" : phone_number;
  }

}

