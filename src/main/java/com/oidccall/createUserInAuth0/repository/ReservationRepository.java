package com.oidccall.createUserInAuth0.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oidccall.createUserInAuth0.entities.Reservation;
import com.oidccall.createUserInAuth0.entities.Restaurateur;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	List<Reservation> findAllByRestaurateurIdAndReservationDate(long restaurateurId, LocalDate reservationDate);

	Optional<Reservation> findByIdAndRestaurateur(long id, Restaurateur restaurateur);
}
