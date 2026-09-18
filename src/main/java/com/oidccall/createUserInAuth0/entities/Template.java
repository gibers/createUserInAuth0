package com.oidccall.createUserInAuth0.entities;

import java.time.Instant;
import java.time.LocalDate;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "template")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Template {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.NONE)
  @Column(name = "id", nullable = false)
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "restaurateur_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private Restaurateur restaurateur;

  @Column(name = "name", length = 150)
  private String name;

  @Builder.Default
  @Column(name = "active", nullable = false)
  private boolean active = true;

  @Builder.Default
  @Column(name = "monday", nullable = false)
  private boolean monday = false;

  @Builder.Default
  @Column(name = "tuesday", nullable = false)
  private boolean tuesday = false;

  @Builder.Default
  @Column(name = "wednesday", nullable = false)
  private boolean wednesday = false;

  @Builder.Default
  @Column(name = "thursday", nullable = false)
  private boolean thursday = false;

  @Builder.Default
  @Column(name = "friday", nullable = false)
  private boolean friday = false;

  @Builder.Default
  @Column(name = "saturday", nullable = false)
  private boolean saturday = false;

  @Builder.Default
  @Column(name = "sunday", nullable = false)
  private boolean sunday = false;

  @Column(name = "data_solo")
  private LocalDate dataSolo;

  @Column(name = "valid_from")
  private LocalDate validFrom;

  @CreatedDate
  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Column(name = "comment", length = 255)
  private String comment;

}