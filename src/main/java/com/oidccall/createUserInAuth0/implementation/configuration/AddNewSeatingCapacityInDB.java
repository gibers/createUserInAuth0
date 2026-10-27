package com.oidccall.createUserInAuth0.implementation.configuration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import com.oidccall.createUserInAuth0.entities.Restaurateur;
import com.oidccall.createUserInAuth0.entities.SeatingCapacity;
import com.oidccall.createUserInAuth0.exceptions.ErrorsEnum;
import com.oidccall.createUserInAuth0.repository.SeatingCapacityRepository;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequestScope
public class AddNewSeatingCapacityInDB {

	private final SeatingCapacityRepository seatingCapacityRepository;
	private final Map<String, String> loadTableColorCodes;

	private Restaurateur restaurateur;
	private Map<String, Integer> paramTableNrCapacity;
	private final Set<@NotNull @Size(max = 20) String> allTableIdInDB = new HashSet<>();
	private final Set<String> allColorCodesInDB = new HashSet<>();

	public AddNewSeatingCapacityInDB(SeatingCapacityRepository seatingCapacityRepository, Map<String, String> loadTableColorCodes) {
		this.seatingCapacityRepository = seatingCapacityRepository;
		this.loadTableColorCodes = loadTableColorCodes;
	}

	void execute(Restaurateur restaurateur, Map<String, Integer> paramTableNrCapacity) {
		this.restaurateur = restaurateur;
		this.paramTableNrCapacity = Map.copyOf(paramTableNrCapacity);
		fillSeatingCapacityFromDB();
		Set<Map.Entry<String, Integer>> newItemsToAddInDB = filterTableIdThatDoesNotExistInDB();
		if (newItemsToAddInDB.isEmpty()) return;
		Set<String> availableColorCodes = getRandomColor();
		saveSeatingInDB(availableColorCodes, newItemsToAddInDB);
	}

	private void fillSeatingCapacityFromDB() {
		seatingCapacityRepository.findAllByRestaurateur(restaurateur).forEach(seatingCapacity -> {
			allTableIdInDB.add(seatingCapacity.getTableNumber());
			allColorCodesInDB.add(seatingCapacity.getColorCode());
		});
	}

	private Set<Map.Entry<String, Integer>> filterTableIdThatDoesNotExistInDB() {
		return this.paramTableNrCapacity.entrySet().stream()
				.filter(x -> !allTableIdInDB.contains(x.getKey()))
				.collect(Collectors.toUnmodifiableSet());
	}

	private void saveSeatingInDB(@NotEmpty Set<String> availableColorCodes, Set<Map.Entry<String, Integer>> collect) {
		Iterator<String> iterator = availableColorCodes.stream().iterator();
		List<SeatingCapacity> listSeatingCapacitiesToAddInDB = new ArrayList<>();
		collect.forEach(item -> {
			String tableNumber = item.getKey();
			Integer newCapacity = item.getValue();
			SeatingCapacity seatingCapacity = SeatingCapacity.builder()
					.restaurateur(restaurateur).tableNumber(tableNumber).capacity(newCapacity)
					.colorCode(iterator.next())
					.build();
			listSeatingCapacitiesToAddInDB.add(seatingCapacity);
		});
		List<SeatingCapacity> seatingCapacities1 = seatingCapacityRepository.saveAll(listSeatingCapacitiesToAddInDB);
		log.debug("number of new Seating capacities saved: {}", seatingCapacities1.size());
	}

	@NotEmpty
	private Set<String> getRandomColor() {
		Set<String> collect = loadTableColorCodes.values().stream()
				.filter(colorCode -> !allColorCodesInDB.contains(colorCode))
				.collect(Collectors.toSet());
		if (collect.isEmpty()) {
			log.error(ErrorsEnum.E_1300.getPrivateErrorMessage());
			throw new DataIntegrityViolationException(ErrorsEnum.E_1300.getPublicErrorMessage());
		}
		return collect;
	}

}

