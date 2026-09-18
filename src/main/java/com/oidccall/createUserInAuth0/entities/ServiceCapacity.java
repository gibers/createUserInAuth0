package com.oidccall.createUserInAuth0.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "service_capacity")
@Inheritance(strategy = InheritanceType.JOINED)
@IdClass(ServiceCapacityId.class)
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ServiceCapacity {

  @Id
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "template_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private Template template;

  @Id
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumns({
      @JoinColumn(name = "restaurateur_id", referencedColumnName = "restaurateur_id", nullable = false),
      @JoinColumn(name = "table_number", referencedColumnName = "table_number", nullable = false)
  })
  @OnDelete(action = OnDeleteAction.CASCADE)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private SeatingCapacity seatingCapacity;

}
