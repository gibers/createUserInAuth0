package com.oidccall.createUserInAuth0.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oidccall.createUserInAuth0.dtos.front.ReservationCardDto;
import com.oidccall.createUserInAuth0.entities.Users;
import com.oidccall.createUserInAuth0.exceptions.UnauthorizedUserAccessException;
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

	@GetMapping("/reservation/{date}/{userId}")
	public List<ReservationCardDto> getAllReservationForThatDate(Authentication authentication,
			@IsoDate @PathVariable String date,
			@PathVariable String userId) {
		if (!userId.equals(authentication.getName())) {
			throw new UnauthorizedUserAccessException("User " + userId + " does not correspond to the authorized user ");
		}
		Users users = (Users) authentication.getDetails();
		LocalDate dateOfTheDay = LocalDate.parse(date);
		return calendarService.getAllReservationForThatDateImpl(users.getId(), dateOfTheDay);
	}

}
