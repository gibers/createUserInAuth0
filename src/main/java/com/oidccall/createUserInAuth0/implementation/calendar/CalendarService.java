package com.oidccall.createUserInAuth0.implementation.calendar;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.oidccall.createUserInAuth0.dtos.front.ReservationCardDto;
import com.oidccall.createUserInAuth0.entities.Restaurateur;
import com.oidccall.createUserInAuth0.repository.ReservationRepository;
import com.oidccall.createUserInAuth0.repository.RestaurateurRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class CalendarService {

	private final RestaurateurRepository restaurateurRepository;
	private final ReservationRepository reservationRepository;

	public List<ReservationCardDto> getAllReservationForThatDateImpl(long userId, LocalDate dateOfTheDay) {
		Restaurateur restaurateur = restaurateurRepository.findByUsersId(userId).orElseThrow();
		// checker le format de la date.
		return reservationRepository.findAllByRestaurateurIdAndReservationDate(restaurateur.getId(), dateOfTheDay)
						.stream().map(ReservationCardDto::new).toList();
	}

}
