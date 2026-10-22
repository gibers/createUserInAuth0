package com.oidccall.createUserInAuth0.implementation.calendar.events;

import java.time.LocalDate;

public record ReservationCreatedEvent(
		long restaurateurId,
		LocalDate reservationDate
) {
}
