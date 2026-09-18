package com.oidccall.createUserInAuth0.entities;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "restaurateur")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Restaurateur {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.NONE)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "name", length = 150)
  private String name;

  @Column(name = "adresse", length = 200)
  private String adresse;

  @Column(name = "telephone", length = 50)
  private String telephone;

  @Column(name = "email", length = 150)
  private String email;

  @CreatedDate
  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Builder.Default
  @Column(name = "active", nullable = false)
  private boolean active = true;

  @Column(name = "monthly_price", precision = 3, scale = 2)
  private BigDecimal monthlyPrice;

  @Column(name = "currency", length = 3)
  private String currency;

}
