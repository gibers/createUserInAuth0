package com.oidccall.createUserInAuth0.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "lunch_service_capacity")
@IdClass(LunchServiceCapacityId.class)
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode
public class LunchServiceCapacity {

  @Id
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "template_id", nullable = false)
  private Template template;

  @Id
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumns({
      @JoinColumn(name = "restaurateur_id", referencedColumnName = "restaurateur_id", nullable = false),
      @JoinColumn(name = "table_number", referencedColumnName = "table_number", nullable = false)
  })
  private SeatingCapacity seatingCapacity;

}
