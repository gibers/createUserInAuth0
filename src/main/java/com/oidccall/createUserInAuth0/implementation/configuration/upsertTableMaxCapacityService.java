package com.oidccall.createUserInAuth0.implementation.configuration;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.oidccall.createUserInAuth0.entities.Restaurateur;
import com.oidccall.createUserInAuth0.entities.SeatingCapacity;
import com.oidccall.createUserInAuth0.entities.Users;
import com.oidccall.createUserInAuth0.repository.RestaurateurRepository;
import com.oidccall.createUserInAuth0.repository.SeatingCapacityRepository;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class upsertTableMaxCapacityService {

	private final RestaurateurRepository restaurateurRepository;
	private final SeatingCapacityRepository seatingCapacityRepository;

	public void upsertTableMaxCapacityImpl(Users users, Map<String, Integer> paramTableNrCapacity) {
		Restaurateur restaurateur = restaurateurRepository.findByUsers(users).orElseThrow();
		List<SeatingCapacity> seatingCapInDB = seatingCapacityRepository.findAllByRestaurateur(restaurateur);

		// we start by updating the capacity in the DB, before to add new seating capacity, otherwise we would update the previous inserted rows.
		this.updateCapacityInDBSeatingCapacity(restaurateur, paramTableNrCapacity);
		this.addNewSeatingCapacityInDB(restaurateur, paramTableNrCapacity);

		// delete all tables that are not in the configuration passed in parameter of this Endpoint.
		List<SeatingCapacity> seatingToRemove = seatingCapInDB.stream()
				.filter(x -> !paramTableNrCapacity.containsKey(x.getTableNumber())).toList();
		this.seatingCapacityRepository.deleteAll(seatingToRemove);
	}

	public Map<@NotNull @Size(max = 20) String, @NotNull @Min(0) @Max(22) Integer> seatingCapacityFromUserIdImpl(long usersId) {
		Restaurateur restaurateur = restaurateurRepository.findByUsersId(usersId).orElseThrow();
		List<SeatingCapacity> allByRestaurateur = seatingCapacityRepository.findAllByRestaurateur(restaurateur);
		return allByRestaurateur.stream().collect(Collectors.toMap(SeatingCapacity::getTableNumber, SeatingCapacity::getCapacity));
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

	private void addNewSeatingCapacityInDB(Restaurateur restaurateur, Map<String, Integer> paramTableNrCapacity) {
		paramTableNrCapacity.forEach((tableNumber, newCapacity) -> {
			Optional<SeatingCapacity> seatingCapacityForTable = this.getSeatingCapacityForTable(restaurateur, tableNumber);
			// we add a new table.
			if (seatingCapacityForTable.isEmpty()) {
				this.saveSeatingInDB(restaurateur, tableNumber, newCapacity);
			}
		});
	}

	private Optional<SeatingCapacity> getSeatingCapacityForTable(Restaurateur restaurateur, String tableNumber) {
		List<SeatingCapacity> seatingCapInDB = seatingCapacityRepository.findAllByRestaurateur(restaurateur);
		return seatingCapInDB.stream().filter(seatingCapacity -> seatingCapacity.getTableNumber().equals(tableNumber))
				.findFirst();
	}

	private void saveSeatingInDB(Restaurateur restaurateur, String tableNumber, int newCapacity) {
		SeatingCapacity seatingCapacity = SeatingCapacity.builder().restaurateur(restaurateur).tableNumber(tableNumber).capacity(newCapacity).build();
		seatingCapacityRepository.save(seatingCapacity);
	}

}
