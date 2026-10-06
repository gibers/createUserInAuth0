package com.oidccall.createUserInAuth0.implementation.template;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.oidccall.createUserInAuth0.dtos.front.SeatingCapacityMax;
import com.oidccall.createUserInAuth0.dtos.front.TemplateWithoutServiceCapacityDto;
import com.oidccall.createUserInAuth0.dtos.mappers.TemplateEntityMapper;
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
public class TemplateService {

	private final RestaurateurRepository restaurateurRepository;
	private final TemplateRepository templateRepository;
	private final SeatingCapacityRepository seatingCapacityRepository;
	private final LunchServiceCapacityRepository lunchServiceCapacityRepository;
	private final DinnerServiceCapacityRepository dinnerServiceCapacityRepository;

	public List<SeatingCapacityMax> addTemplateImpl(long userId) {
		Restaurateur restaurateur = restaurateurRepository.findByUsersId(userId).orElseThrow();

		// Create a new template
		Template templateName = Template.builder().name("template's Name")
				.restaurateur(restaurateur)
				.validFrom(LocalDate.now())
				.build();
		Template savedTemplate = this.templateRepository.save(templateName);

		List<SeatingCapacity> allByRestaurateur = seatingCapacityRepository.findAllByRestaurateur(restaurateur);

		// Create new LunchServiceCapacity and DinnerServiceCapacity for each SeatingCapacity.
		allByRestaurateur.forEach(x -> {
			LunchServiceCapacity lunchServiceCapacity = LunchServiceCapacity.builder()
					.template(savedTemplate)
					.seatingCapacity(x)
					.build();
			lunchServiceCapacityRepository.save(lunchServiceCapacity);

			DinnerServiceCapacity dinnerServiceCapacity = DinnerServiceCapacity.builder()
					.template(savedTemplate)
					.seatingCapacity(x)
					.build();
			dinnerServiceCapacityRepository.save(dinnerServiceCapacity);
		});

		List<LunchServiceCapacity> lunchByTemplateId = this.lunchServiceCapacityRepository.findByTemplateId(savedTemplate.getId());
		List<DinnerServiceCapacity> dinnerByTemplateId = this.dinnerServiceCapacityRepository.findByTemplateId(savedTemplate.getId());

		lunchByTemplateId.get(0).getSeatingCapacity().getTableNumber();
		lunchByTemplateId.get(0).getSeatingCapacity().getCapacity();

		List<SeatingCapacityMax> list = lunchByTemplateId.stream()
				.map(seatingCapacity -> new SeatingCapacityMax(
						seatingCapacity.getSeatingCapacity().getTableNumber(),
						seatingCapacity.getSeatingCapacity().getCapacity()))
				.toList();

		return list;
	}

	public List<TemplateWithoutServiceCapacityDto> getAllTemplateImpl(long userId) {
		Restaurateur restaurateur = restaurateurRepository.findByUsersId(userId).orElseThrow();
		List<Template> templates = restaurateur.getTemplates();
		return TemplateEntityMapper.mapToTemplateWithoutSCDto(templates);
	}

}
