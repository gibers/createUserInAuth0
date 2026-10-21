package com.oidccall.createUserInAuth0.implementation.calendar;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.oidccall.createUserInAuth0.implementation.calendar.events.ReservationCreatedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationCreatedEventListener {

	private final CalendarNotificationService calendarNotificationService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onReservationCreated(ReservationCreatedEvent event) {
		calendarNotificationService.sendReservationCreated(event);
	}
}
