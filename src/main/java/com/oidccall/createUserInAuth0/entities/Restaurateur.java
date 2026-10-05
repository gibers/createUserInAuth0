package com.oidccall.createUserInAuth0.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "users_id", nullable = false, unique = true)
  private Users users;

  @OneToMany(mappedBy = "restaurateur", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<Template> templates = new ArrayList<>();

  @OneToMany(mappedBy = "restaurateur", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<SeatingCapacity> seatingCapacities = new ArrayList<>();

  @Transient
  public List<Map.Entry<String, Integer>> getMapTableIdCapacitySortedByCapacity() {
    return seatingCapacities.stream()
        .sorted(Comparator.comparing(SeatingCapacity::getCapacity)
            .thenComparing(SeatingCapacity::getTableNumber))
        .map(seatingCapacity -> Map.entry(
            seatingCapacity.getTableNumber(),
            seatingCapacity.getCapacity()))
        .toList();
  }

}
