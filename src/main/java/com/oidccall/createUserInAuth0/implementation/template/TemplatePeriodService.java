package com.oidccall.createUserInAuth0.implementation.template;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.oidccall.createUserInAuth0.dtos.front.TemplateWithoutServiceCapacityDto;
import com.oidccall.createUserInAuth0.entities.Restaurateur;
import com.oidccall.createUserInAuth0.entities.Template;
import com.oidccall.createUserInAuth0.exceptions.ErrorsEnum;
import com.oidccall.createUserInAuth0.implementation.template.utils.TemplateUtilsCheckSingleDate;
import com.oidccall.createUserInAuth0.implementation.template.utils.TemplateUtilsDaysOfWeek;
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
				restaurateur.getTemplates().stream()
						.filter(Template::isActive)
						.filter(template -> Objects.equals(template.getId(), templateWithoutServiceCapacityDto.getId()))
						.findFirst().orElseThrow(() -> {
							log.error(ErrorsEnum.E_1102.getPrivateErrorMessage(), templateWithoutServiceCapacityDto.getId());
							return new DataIntegrityViolationException(ErrorsEnum.E_1102.getPublicErrorMessage());
						});

		onlyOneDaysPerTemplateShouldBeSelectedOrThrow(restaurateur, templateFromDB.getId(), templateWithoutServiceCapacityDto);
		setDaysOfWeek(templateWithoutServiceCapacityDto, templateFromDB);
		setDateSoloIfPossible(templateWithoutServiceCapacityDto, templateFromDB);
		templateRepository.save(templateFromDB);
	}

	public void activeImpl(long userId, TemplateWithoutServiceCapacityDto templateWithoutServiceCapacityDto) {
		Restaurateur restaurateur = restaurateurRepository.findByUsersId(userId).orElseThrow();
		Template templateToModify = restaurateur.getTemplates().stream()
				.filter(template -> Objects.equals(template.getId(), templateWithoutServiceCapacityDto.getId()))
				.findFirst().orElseThrow();

		if (templateToModify.isActive() == templateWithoutServiceCapacityDto.isActive()) {
			return;
		}

		// 1. Whether we switch from active to inactive or vice versa, we clear the template.
		TemplateUtilsCheckSingleDate.putAllDaysAtFalse(templateToModify);
		templateToModify.setDateSolo(null);
		templateToModify.setActive(templateWithoutServiceCapacityDto.isActive());
		templateRepository.save(templateToModify);
	}

	/**
	 * Should be called after {@link #setDaysOfWeek(TemplateWithoutServiceCapacityDto, Template)}.
	 */
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

	private void onlyOneDaysPerTemplateShouldBeSelectedOrThrow(Restaurateur restaurateur,
			Long templateFromDBId, TemplateWithoutServiceCapacityDto templateWithoutServiceCapacityDto) {

		TemplateWithoutServiceCapacityDto combinedTemplate = getCombinedTemplate(restaurateur, templateFromDBId);
		Optional<String> dayConflict = TemplateUtilsDaysOfWeek.bothDaysAreTrue().stream()
				.map(x -> x.apply(combinedTemplate, templateWithoutServiceCapacityDto))
				.filter(x -> !x.isEmpty())
				.findFirst();

		if (dayConflict.isPresent()) {
			log.error(ErrorsEnum.E_1101.getPrivateErrorMessage(), dayConflict.get());
			throw new DataIntegrityViolationException(ErrorsEnum.E_1101.getPublicErrorMessage());
		}
	}

	private TemplateWithoutServiceCapacityDto getCombinedTemplate(Restaurateur restaurateur, Long templateIdToExclude) {
		List<Template> filteredTemplates = restaurateur.getTemplates().stream()
				.filter(t -> !Objects.equals(t.getId(), templateIdToExclude) && t.isActive())
				.toList();

		TemplateWithoutServiceCapacityDto.TemplateWithoutServiceCapacityDtoBuilder builder = TemplateWithoutServiceCapacityDto.builder();
		builder.monday(TemplateUtilsDaysOfWeek.atLeastOneMondayIsTrue(filteredTemplates));
		builder.tuesday(TemplateUtilsDaysOfWeek.atLeastOneTuesdayIsTrue(filteredTemplates));
		builder.wednesday(TemplateUtilsDaysOfWeek.atLeastOneWednesdayIsTrue(filteredTemplates));
		builder.thursday(TemplateUtilsDaysOfWeek.atLeastOneThursdayIsTrue(filteredTemplates));
		builder.friday(TemplateUtilsDaysOfWeek.atLeastOneFridayIsTrue(filteredTemplates));
		builder.saturday(TemplateUtilsDaysOfWeek.atLeastOneSaturdayIsTrue(filteredTemplates));
		builder.sunday(TemplateUtilsDaysOfWeek.atLeastOneSundayIsTrue(filteredTemplates));
		return builder.build();
	}

}
