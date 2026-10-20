package com.oidccall.createUserInAuth0.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oidccall.createUserInAuth0.entities.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

	List<Reservation> findAllByRestaurateurIdAndReservationDate(long restaurateurId, LocalDate reservationDate);

}
