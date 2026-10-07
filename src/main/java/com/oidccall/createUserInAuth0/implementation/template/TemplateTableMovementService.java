package com.oidccall.createUserInAuth0.implementation.template;

import java.util.Set;
import java.util.function.BiConsumer;

import org.springframework.stereotype.Service;

import com.oidccall.createUserInAuth0.dtos.front.TableMovementDto;
import com.oidccall.createUserInAuth0.entities.DinnerServiceCapacity;
import com.oidccall.createUserInAuth0.entities.LunchServiceCapacity;
import com.oidccall.createUserInAuth0.entities.Restaurateur;
import com.oidccall.createUserInAuth0.entities.SeatingCapacity;
import com.oidccall.createUserInAuth0.entities.Template;
import com.oidccall.createUserInAuth0.repository.DinnerServiceCapacityRepository;
import com.oidccall.createUserInAuth0.repository.LunchServiceCapacityRepository;
import com.oidccall.createUserInAuth0.repository.RestaurateurRepository;
import com.oidccall.createUserInAuth0.repository.SeatingCapacityRepository;
import com.oidccall.createUserInAuth0.repository.TemplateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class TemplateTableMovementService {

	private final RestaurateurRepository restaurateurRepository;
	private final SeatingCapacityRepository seatingCapacityRepository;
	private final TemplateRepository templateRepository;
	private final LunchServiceCapacityRepository lunchServiceCapacityRepository;
	private final DinnerServiceCapacityRepository dinnerServiceCapacityRepository;
	private final BiConsumer<Set<String>, String> removeTableIdFromTemplate = (Set<String> setSeatingCapacity, String tableIdParam) ->
			setSeatingCapacity.stream()
					.filter(tableId -> tableId.equals(tableIdParam))
					.findFirst()
					.ifPresent(setSeatingCapacity::remove);

	public void moveTableToBookableImpl(long userId, TableMovementDto tableMovementDto) {
		Restaurateur restaurateur = restaurateurRepository.findByUsersId(userId).orElseThrow();
		// 1. check the table exists for that restaurateur
		SeatingCapacity seatingCapacityFromDB =
				this.seatingCapacityRepository.findAllByRestaurateurAndTableNumber(restaurateur, tableMovementDto.tableId()).orElseThrow();
		// 2. check the templateId exists for that restaurateur
		Template templateFromDB = this.templateRepository.findAllByIdAndRestaurateur(tableMovementDto.templateId(), restaurateur).orElseThrow();
		// 3. insert the table number into the related template.
		switch (tableMovementDto.serviceTypeDto()) {
			case LUNCH -> newTableInLunchServiceCapacityDB(templateFromDB, seatingCapacityFromDB);
			case DINNER -> newTableInDinnerServiceCapacityDB(templateFromDB, seatingCapacityFromDB);
			default -> throw new IllegalArgumentException("Invalid service type");
		}
	}

	public void removeTableFromToBookableImpl(long userId, TableMovementDto tableMovementDto) {
		Restaurateur restaurateur = restaurateurRepository.findByUsersId(userId).orElseThrow();
		// 1. check the templateId exists for that restaurateur
		Template templateFromDB = this.templateRepository.findAllByIdAndRestaurateur(tableMovementDto.templateId(), restaurateur).orElseThrow();
		switch (tableMovementDto.serviceTypeDto()) {
			case LUNCH -> removeTableIdFromTemplate.accept(templateFromDB.getLunchTableNumbers(), tableMovementDto.tableId());
			case DINNER -> removeTableIdFromTemplate.accept(templateFromDB.getDinnerTableNumbers(), tableMovementDto.tableId());
			default -> throw new IllegalArgumentException("Invalid service type");
		}
		this.templateRepository.save(templateFromDB);
	}

	private void newTableInDinnerServiceCapacityDB(Template templateFromDB, SeatingCapacity seatingCapacityFromDB) {
		var dinnerServiceCapacity = new DinnerServiceCapacity();
		dinnerServiceCapacity.setTemplate(templateFromDB);
		dinnerServiceCapacity.setSeatingCapacity(seatingCapacityFromDB);
		this.dinnerServiceCapacityRepository.save(dinnerServiceCapacity);
	}

	private void newTableInLunchServiceCapacityDB(Template templateFromDB, SeatingCapacity seatingCapacityFromDB) {
		var lunchServiceCapacity = new LunchServiceCapacity();
		lunchServiceCapacity.setTemplate(templateFromDB);
		lunchServiceCapacity.setSeatingCapacity(seatingCapacityFromDB);
		this.lunchServiceCapacityRepository.save(lunchServiceCapacity);
	}

}
