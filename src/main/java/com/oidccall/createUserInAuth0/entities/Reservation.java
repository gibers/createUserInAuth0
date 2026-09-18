package com.oidccall.createUserInAuth0.entities;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

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
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "reservation")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Reservation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.NONE)
  @Column(name = "id", nullable = false)
  private Integer id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "restaurateur_id", nullable = false, insertable = false, updatable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private Restaurateur restaurateur;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumns({
      @JoinColumn(name = "template_id", referencedColumnName = "template_id", nullable = false),
      @JoinColumn(name = "restaurateur_id", referencedColumnName = "restaurateur_id", nullable = false),
      @JoinColumn(name = "table_number", referencedColumnName = "table_number", nullable = false)
  })
  @OnDelete(action = OnDeleteAction.CASCADE)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private ServiceCapacity serviceCapacity;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "client_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private Client client;

  @NotNull
  @Column(name = "reservation_date", nullable = false)
  private LocalDate reservationDate;

  @NotNull
  @Column(name = "start_time", nullable = false)
  private LocalTime startTime;

  @NotNull
  @Column(name = "end_time", nullable = false)
  private LocalTime endTime;

  @NotNull
  @Positive
  @Column(name = "nb_person", nullable = false)
  private int nbPerson;

  @CreatedDate
  @Column(name = "create_at", nullable = false)
  private Instant createAt;

  @LastModifiedDate
  @Column(name = "update_at", nullable = false)
  private Instant updateAt;

}