package com.oidccall.createUserInAuth0.implementation.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.oidccall.createUserInAuth0.dtos.front.ServiceTypeDto;
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

class TemplateTableMovementServiceTest {

	private final RestaurateurRepository restaurateurRepository = mock(RestaurateurRepository.class);
	private final SeatingCapacityRepository seatingCapacityRepository = mock(SeatingCapacityRepository.class);
	private final TemplateRepository templateRepository = mock(TemplateRepository.class);
	private final LunchServiceCapacityRepository lunchServiceCapacityRepository = mock(LunchServiceCapacityRepository.class);
	private final DinnerServiceCapacityRepository dinnerServiceCapacityRepository = mock(DinnerServiceCapacityRepository.class);

	private final TemplateTableMovementService service = new TemplateTableMovementService(
			restaurateurRepository,
			seatingCapacityRepository,
			templateRepository,
			lunchServiceCapacityRepository,
			dinnerServiceCapacityRepository);

	private Restaurateur restaurateur;
	private SeatingCapacity seatingCapacity;
	private Template template;

	@BeforeEach
	void setUp() {
		restaurateur = new Restaurateur();
		seatingCapacity = new SeatingCapacity();
		template = new Template();

		when(restaurateurRepository.findByUsersId(42L)).thenReturn(Optional.of(restaurateur));
		when(seatingCapacityRepository.findAllByRestaurateurAndTableNumber(restaurateur, "TABLE-01"))
				.thenReturn(Optional.of(seatingCapacity));
		when(templateRepository.findAllByIdAndRestaurateur(7L, restaurateur)).thenReturn(Optional.of(template));
	}

	@Test
	void moveTableToBookableImpl_savesLunchCapacityWithResolvedTemplateAndTable() {
		var dto = new TableMovementDto(7L, "TABLE-01", ServiceTypeDto.LUNCH);
		var captor = ArgumentCaptor.forClass(LunchServiceCapacity.class);

		service.moveTableToBookableImpl(42L, dto);

		verify(restaurateurRepository).findByUsersId(42L);
		verify(seatingCapacityRepository).findAllByRestaurateurAndTableNumber(restaurateur, "TABLE-01");
		verify(templateRepository).findAllByIdAndRestaurateur(7L, restaurateur);
		verify(lunchServiceCapacityRepository).save(captor.capture());
		assertSame(template, captor.getValue().getTemplate());
		assertSame(seatingCapacity, captor.getValue().getSeatingCapacity());
		verify(dinnerServiceCapacityRepository, never()).save(any(DinnerServiceCapacity.class));
	}

	@Test
	void moveTableToBookableImpl_savesDinnerCapacityWithResolvedTemplateAndTable() {
		var dto = new TableMovementDto(7L, "TABLE-01", ServiceTypeDto.DINNER);
		var captor = ArgumentCaptor.forClass(DinnerServiceCapacity.class);

		service.moveTableToBookableImpl(42L, dto);

		verify(restaurateurRepository).findByUsersId(42L);
		verify(seatingCapacityRepository).findAllByRestaurateurAndTableNumber(restaurateur, "TABLE-01");
		verify(templateRepository).findAllByIdAndRestaurateur(7L, restaurateur);
		verify(dinnerServiceCapacityRepository).save(captor.capture());
		assertSame(template, captor.getValue().getTemplate());
		assertSame(seatingCapacity, captor.getValue().getSeatingCapacity());
		verify(lunchServiceCapacityRepository, never()).save(any(LunchServiceCapacity.class));
	}

	@Test
	void removeTableFromToBookableImpl_removesLunchTableAndSavesTemplate() {
		template.setLunchTableNumbers(new HashSet<>(Set.of("TABLE-01", "TABLE-02")));
		template.setDinnerTableNumbers(new HashSet<>(Set.of("TABLE-01")));
		var dto = new TableMovementDto(7L, "TABLE-01", ServiceTypeDto.LUNCH);

		service.removeTableFromToBookableImpl(42L, dto);

		assertEquals(Set.of("TABLE-02"), template.getLunchTableNumbers());
		assertEquals(Set.of("TABLE-01"), template.getDinnerTableNumbers());
		verify(templateRepository).save(template);
	}

	@Test
	void removeTableFromToBookableImpl_removesDinnerTableAndSavesTemplate() {
		template.setLunchTableNumbers(new HashSet<>(Set.of("TABLE-01")));
		template.setDinnerTableNumbers(new HashSet<>(Set.of("TABLE-01", "TABLE-02")));
		var dto = new TableMovementDto(7L, "TABLE-01", ServiceTypeDto.DINNER);

		service.removeTableFromToBookableImpl(42L, dto);

		assertEquals(Set.of("TABLE-01"), template.getLunchTableNumbers());
		assertEquals(Set.of("TABLE-02"), template.getDinnerTableNumbers());
		verify(templateRepository).save(template);
	}
}
