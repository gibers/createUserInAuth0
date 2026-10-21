package com.oidccall.createUserInAuth0.dtos.front;

import java.time.LocalDate;
import java.time.LocalTime;

import com.oidccall.createUserInAuth0.validation.PhoneNumber;

public record ReservationCardRequestDto(
		long restaurantId,
		LocalDate dateReservation,
		LocalTime startTime,
		int nbPerson,
		String name,
		String firstname,
		@PhoneNumber String phone) {
}
