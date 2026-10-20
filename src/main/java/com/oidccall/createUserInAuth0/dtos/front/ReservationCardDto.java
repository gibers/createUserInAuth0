package com.oidccall.createUserInAuth0.dtos.front;

import java.time.LocalTime;

import com.oidccall.createUserInAuth0.entities.Reservation;
import com.oidccall.createUserInAuth0.validation.PhoneNumber;

public record ReservationCardDto(
		long idReservation,
		String tableNumber,
		LocalTime startTime,
		LocalTime endTime,
		int nbPerson,
		String name,
		String firstname,
		@PhoneNumber String phone,
		String code
) {

  public ReservationCardDto(Reservation reservation) {
    this(
        reservation.getId(),
        reservation.getSeatingCapacity().getTableNumber(),
        reservation.getStartTime(),
        reservation.getEndTime(),
        reservation.getNbPerson(),
        reservation.getClient().getName(),
        reservation.getClient().getFirstname(),
        reservation.getClient().getPhone(),
        reservation.getClient().getCode()
    );
  }

}
