package com.oidccall.createUserInAuth0.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class RestaurateurAndSeatingCapacityEntityTest {

  private static Validator validator;

  @BeforeAll
  static void setUp() {
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      validator = factory.getValidator();
    }
  }

  @Test
  void testRestaurateurEntityCreationAndLombok() {
    Instant now = Instant.now();
    Restaurateur restaurateur = Restaurateur.builder()
        .name("Le Gourmet")
        .adresse("123 Rue de Paris")
        .telephone("+33123456789")
        .email("contact@legourmet.fr")
        .createdAt(now)
        .updatedAt(now)
        .active(true)
        .monthlyPrice(new BigDecimal("9.99"))
        .currency("EUR")
        .build();

    assertNotNull(restaurateur);
    assertEquals("Le Gourmet", restaurateur.getName());
    assertEquals("123 Rue de Paris", restaurateur.getAdresse());
    assertEquals("+33123456789", restaurateur.getTelephone());
    assertEquals("contact@legourmet.fr", restaurateur.getEmail());
    assertEquals(now, restaurateur.getCreatedAt());
    assertEquals(now, restaurateur.getUpdatedAt());
    assertTrue(restaurateur.isActive());
    assertEquals(new BigDecimal("9.99"), restaurateur.getMonthlyPrice());
    assertEquals("EUR", restaurateur.getCurrency());
    assertNotNull(restaurateur.toString());
  }

  @Test
  void testSeatingCapacityEntityCreationAndLombok() {
    Instant now = Instant.now();
    Restaurateur restaurateur = Restaurateur.builder()
        .name("Le Gourmet")
        .build();

    SeatingCapacity seatingCapacity = SeatingCapacity.builder()
        .restaurateur(restaurateur)
        .tableNumber("T10")
        .capacity(4)
        .createdAt(now)
        .updatedAt(now)
        .build();

    assertNotNull(seatingCapacity);
    assertEquals(restaurateur, seatingCapacity.getRestaurateur());
    assertEquals("T10", seatingCapacity.getTableNumber());
    assertEquals(4, seatingCapacity.getCapacity());
    assertEquals(now, seatingCapacity.getCreatedAt());
    assertEquals(now, seatingCapacity.getUpdatedAt());
    assertNotNull(seatingCapacity.toString());

    Set<ConstraintViolation<SeatingCapacity>> violations = validator.validate(seatingCapacity);
    assertTrue(violations.isEmpty());
  }

  @Test
  void testSeatingCapacityId() {
    SeatingCapacityId id1 = new SeatingCapacityId(1L, "T1");
    SeatingCapacityId id2 = SeatingCapacityId.builder().restaurateur(1L).tableNumber("T1").build();

    assertEquals(id1, id2);
    assertEquals(id1.hashCode(), id2.hashCode());
    assertEquals(1L, id1.getRestaurateur());
    assertEquals("T1", id1.getTableNumber());
    assertEquals("SeatingCapacityId(restaurateur=1, tableNumber=T1)", id1.toString());
  }

  @Test
  void testSeatingCapacityValidationConstraints() {
    Instant now = Instant.now();
    SeatingCapacity invalidSeatingCapacity = SeatingCapacity.builder()
        .tableNumber("12345678901") // exceeds max 10
        .capacity(25)              // exceeds max 22
        .createdAt(now)
        .updatedAt(now)
        .build();

    Set<ConstraintViolation<SeatingCapacity>> violations = validator.validate(invalidSeatingCapacity);
    assertFalse(violations.isEmpty());
    assertEquals(3, violations.size()); // restaurateur is null, tableNumber > 10 chars, capacity > 22
  }
}
