package com.oidccall.createUserInAuth0.implementation.calendar.events;

import java.time.LocalDate;

public record ReservationDeletedEvent(
		long restaurateurId,
		LocalDate reservationDate
) {
}
