package com.oidccall.createUserInAuth0.implementation.template;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.oidccall.createUserInAuth0.dtos.front.TemplateWithoutServiceCapacityDto;
import com.oidccall.createUserInAuth0.entities.Restaurateur;
import com.oidccall.createUserInAuth0.entities.Template;
import com.oidccall.createUserInAuth0.implementation.template.utils.TemplateUtilsCheckSingleDate;
import com.oidccall.createUserInAuth0.repository.RestaurateurRepository;
import com.oidccall.createUserInAuth0.repository.TemplateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class TemplatePeriodService {

	private final RestaurateurRepository restaurateurRepository;
	private final TemplateRepository templateRepository;

	public void modifyDaysOfWeekOrSingleDateImpl(long userId, TemplateWithoutServiceCapacityDto templateWithoutServiceCapacityDto) {
		Restaurateur restaurateur = restaurateurRepository.findByUsersId(userId).orElseThrow();
		Template templateFromDB =
				restaurateur.getTemplates().stream().filter(template -> Objects.equals(template.getId(),
								templateWithoutServiceCapacityDto.getId()))
						.findFirst().orElseThrow();

		setDaysOfWeek(templateWithoutServiceCapacityDto, templateFromDB);
		setDateSoloIfPossible(templateWithoutServiceCapacityDto, templateFromDB);
		templateFromDB.setActive(templateWithoutServiceCapacityDto.isActive());
		templateRepository.save(templateFromDB);
	}

	private static void setDateSoloIfPossible(TemplateWithoutServiceCapacityDto templateWithoutServiceCapacityDto, Template templateFromDB) {
		boolean atLeastOneIsTrue = TemplateUtilsCheckSingleDate.atLeastOneDayOfWeekIsTrue(templateFromDB);
		if (atLeastOneIsTrue) {
			templateFromDB.setDateSolo(null);
		} else {
			templateFromDB.setDateSolo(templateWithoutServiceCapacityDto.getDateSolo());
		}
	}

	private static void setDaysOfWeek(TemplateWithoutServiceCapacityDto templateWithoutServiceCapacityDto, Template templateFromDB) {
		templateFromDB.setMonday(templateWithoutServiceCapacityDto.isMonday());
		templateFromDB.setTuesday(templateWithoutServiceCapacityDto.isTuesday());
		templateFromDB.setWednesday(templateWithoutServiceCapacityDto.isWednesday());
		templateFromDB.setThursday(templateWithoutServiceCapacityDto.isThursday());
		templateFromDB.setFriday(templateWithoutServiceCapacityDto.isFriday());
		templateFromDB.setSaturday(templateWithoutServiceCapacityDto.isSaturday());
		templateFromDB.setSunday(templateWithoutServiceCapacityDto.isSunday());
	}

}
