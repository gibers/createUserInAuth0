package com.oidccall.createUserInAuth0.implementation.calendar.events;

import java.time.LocalDate;

import com.oidccall.createUserInAuth0.dtos.front.ReservationCardResponseDto;

public record ReservationCreatedEvent(
		long restaurateurId,
		LocalDate reservationDate,
		ReservationCardResponseDto reservation
) {
}
