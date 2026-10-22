package com.oidccall.createUserInAuth0.implementation.calendar;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.oidccall.createUserInAuth0.dtos.front.ReservationCardResponseDto;
import com.oidccall.createUserInAuth0.entities.Client;
import com.oidccall.createUserInAuth0.entities.Reservation;
import com.oidccall.createUserInAuth0.entities.Restaurateur;
import com.oidccall.createUserInAuth0.repository.ClientRepository;
import com.oidccall.createUserInAuth0.repository.ReservationRepository;
import com.oidccall.createUserInAuth0.repository.RestaurateurRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class CalendarService {

	private final RestaurateurRepository restaurateurRepository;
	private final ReservationRepository reservationRepository;
	private final ClientRepository clientRepository;

	public List<ReservationCardResponseDto> getAllReservationForThatDateImpl(long userId, LocalDate dateOfTheDay) {
		Restaurateur restaurateur = restaurateurRepository.findByUsersId(userId).orElseThrow();
		return reservationRepository.findAllByRestaurateurIdAndReservationDate(restaurateur.getId(), dateOfTheDay)
				.stream().map(ReservationCardResponseDto::new).toList();
	}

	@Transactional
	public void deleteReservationImpl(long userId, long idReservation) {
		Restaurateur restaurateur = restaurateurRepository.findByUsersId(userId).orElseThrow();
		Reservation reservationToDelete = this.reservationRepository.findByIdAndRestaurateur(idReservation, restaurateur).orElseThrow();
		Client client = reservationToDelete.getClient();
		reservationRepository.delete(reservationToDelete);
		clientRepository.delete(client);
	}

}
