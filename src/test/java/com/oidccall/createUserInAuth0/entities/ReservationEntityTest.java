package com.oidccall.createUserInAuth0.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class ReservationEntityTest {

  private static Validator validator;

  @BeforeAll
  static void setUp() {
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      validator = factory.getValidator();
    }
  }

  @Test
  void testReservationEntityCreationAndLombok() {
    Instant now = Instant.now();
    LocalDate today = LocalDate.now();
    LocalTime startTime = LocalTime.of(12, 0);
    LocalTime endTime = LocalTime.of(14, 0);

    Restaurateur restaurateur = Restaurateur.builder()
        .name("Le Gourmet")
        .build();

    Client client = Client.builder()
        .name("Dupont")
        .firstname("Jean")
        .build();

    SeatingCapacity seatingCapacity = SeatingCapacity.builder()
        .restaurateur(restaurateur)
        .tableNumber("T1")
        .capacity(4)
        .build();

    Reservation reservation = Reservation.builder()
        .restaurateur(restaurateur)
        .seatingCapacity(seatingCapacity)
        .client(client)
        .reservationDate(today)
        .startTime(startTime)
        .endTime(endTime)
        .nbPerson(4)
        .createAt(now)
        .updateAt(now)
        .build();

    assertNotNull(reservation);
    assertEquals(restaurateur, reservation.getRestaurateur());
    assertEquals(seatingCapacity, reservation.getSeatingCapacity());
    assertEquals(client, reservation.getClient());
    assertEquals(today, reservation.getReservationDate());
    assertEquals(startTime, reservation.getStartTime());
    assertEquals(endTime, reservation.getEndTime());
    assertEquals(4, reservation.getNbPerson());
    assertEquals(now, reservation.getCreateAt());
    assertEquals(now, reservation.getUpdateAt());
    assertNotNull(reservation.toString());

    Set<ConstraintViolation<Reservation>> violations = validator.validate(reservation);
    assertTrue(violations.isEmpty());
  }
}
