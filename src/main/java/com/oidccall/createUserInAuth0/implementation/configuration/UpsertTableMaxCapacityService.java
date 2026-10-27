package com.oidccall.createUserInAuth0.implementation.configuration;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.oidccall.createUserInAuth0.dtos.front.SeatingCapacityMax;
import com.oidccall.createUserInAuth0.dtos.mappers.SeatingCapacityMaxMapper;
import com.oidccall.createUserInAuth0.entities.Restaurateur;
import com.oidccall.createUserInAuth0.entities.SeatingCapacity;
import com.oidccall.createUserInAuth0.entities.Users;
import com.oidccall.createUserInAuth0.repository.RestaurateurRepository;
import com.oidccall.createUserInAuth0.repository.SeatingCapacityRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class UpsertTableMaxCapacityService {

	private final RestaurateurRepository restaurateurRepository;
	private final SeatingCapacityRepository seatingCapacityRepository;
	private final AddNewSeatingCapacityInDB addNewSeatingCapacityInDB;

	@Transactional
	public void upsertTableMaxCapacityImpl(Users users, List<SeatingCapacityMax> seatingCapacityMax) {
		Map<String, Integer> paramTableNrCapacity = seatingCapacityMax.stream()
				.collect(Collectors.toMap(SeatingCapacityMax::tableId, SeatingCapacityMax::capacity));
		Restaurateur restaurateur = restaurateurRepository.findByUsers(users).orElseThrow();
		List<SeatingCapacity> seatingCapInDB = seatingCapacityRepository.findAllByRestaurateur(restaurateur);

		// 1. we start by updating the capacity in the DB, before to add new seating capacity, otherwise we would update the previous inserted
		//  rows.
		this.updateCapacityInDBSeatingCapacity(restaurateur, paramTableNrCapacity);
		// 2. delete all tables that are not in the configuration passed in parameter of this Endpoint.
		List<SeatingCapacity> seatingToRemove = seatingCapInDB.stream()
				.filter(x -> !paramTableNrCapacity.containsKey(x.getTableNumber())).toList();
		this.seatingCapacityRepository.deleteAll(seatingToRemove);
		// 3. add new tables in DB
		this.addNewSeatingCapacityInDB.execute(restaurateur, paramTableNrCapacity);
	}

	public List<SeatingCapacityMax> seatingCapacityFromUserIdImpl(long usersId) {
		Restaurateur restaurateur = restaurateurRepository.findByUsersId(usersId).orElseThrow();
		List<SeatingCapacity> allByRestaurateur = seatingCapacityRepository.findAllByRestaurateur(restaurateur);
		return SeatingCapacityMaxMapper.mapToSeatingCapacityMax(allByRestaurateur);
	}

	private void updateCapacityInDBSeatingCapacity(Restaurateur restaurateur, Map<String, Integer> paramTableNrCapacity) {
		paramTableNrCapacity.entrySet().stream()
				.filter(entry -> this.getSeatingCapacityForTable(restaurateur, entry.getKey()).isPresent())
				.forEach(entry -> {
					SeatingCapacity seatingCapacityExisting = this.getSeatingCapacityForTable(restaurateur, entry.getKey()).orElseThrow();
					if (seatingCapacityExisting.getCapacity() <= entry.getValue()) { // newCapacity is bigger
						seatingCapacityExisting.setCapacity(entry.getValue());
					} else { // newCapacity is smaller
						//          todo: we need to check if this table is reserved, and block the modification if necessary.
						seatingCapacityExisting.setCapacity(entry.getValue());
					}
				});
	}

	private Optional<SeatingCapacity> getSeatingCapacityForTable(Restaurateur restaurateur, String tableNumber) {
		List<SeatingCapacity> seatingCapInDB = seatingCapacityRepository.findAllByRestaurateur(restaurateur);
		return seatingCapInDB.stream().filter(seatingCapacity -> seatingCapacity.getTableNumber().equals(tableNumber))
				.findFirst();
	}

}
