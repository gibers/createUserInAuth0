package com.oidccall.createUserInAuth0.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.oidccall.createUserInAuth0.dtos.front.ReservationCardRequestDto;
import com.oidccall.createUserInAuth0.dtos.front.ReservationCardResponseDto;
import com.oidccall.createUserInAuth0.entities.Users;
import com.oidccall.createUserInAuth0.exceptions.UnauthorizedUserAccessException;
import com.oidccall.createUserInAuth0.implementation.calendar.CalendarAddReservationService;
import com.oidccall.createUserInAuth0.implementation.calendar.CalendarNotificationService;
import com.oidccall.createUserInAuth0.implementation.calendar.CalendarService;
import com.oidccall.createUserInAuth0.validation.IsoDate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar")
@Slf4j
@Validated
public class CalendarController {

	private final CalendarService calendarService;
	private final CalendarAddReservationService calendarAddReservationService;
	private final CalendarNotificationService calendarNotificationService;

	@GetMapping(value = "/notifications/{date}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter subscribeToReservationNotifications(Authentication authentication, @IsoDate @PathVariable String date) {
		Users users = (Users) authentication.getDetails();
		LocalDate dateOfTheDay = LocalDate.parse(date);
		return calendarNotificationService.subscribe(users.getId(), dateOfTheDay);
	}

	@GetMapping("/reservation/{date}/{userId}")
	public List<ReservationCardResponseDto> getAllReservationForThatDate(Authentication authentication,
			@IsoDate @PathVariable String date,
			@PathVariable String userId) {
		if (!userId.equals(authentication.getName())) {
			throw new UnauthorizedUserAccessException("User " + userId + " does not correspond to the authorized user ");
		}
		Users users = (Users) authentication.getDetails();
		LocalDate dateOfTheDay = LocalDate.parse(date);
		return calendarService.getAllReservationForThatDateImpl(users.getId(), dateOfTheDay);
	}

	@PostMapping("/addReservation/")
	public void addReservation(@RequestBody ReservationCardRequestDto reservationCardRequestDto) {
		calendarAddReservationService.addReservationImpl(reservationCardRequestDto);
	}

	@DeleteMapping("/reservation/{idReservation}/{userId}")
	public void deleteReservation(Authentication authentication,
			@PathVariable long idReservation,
			@PathVariable String userId) {
		if (!userId.equals(authentication.getName())) {
			throw new UnauthorizedUserAccessException("User " + userId + " does not correspond to the authorized user ");
		}
		Users users = (Users) authentication.getDetails();
		calendarService.deleteReservationImpl(users.getId(), idReservation);
	}

}
