package com.oidccall.createUserInAuth0.implementation.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.oidccall.createUserInAuth0.dtos.front.SeatingCapacityMax;
import com.oidccall.createUserInAuth0.dtos.mappers.SeatingCapacityMaxMapper;
import com.oidccall.createUserInAuth0.entities.Restaurateur;
import com.oidccall.createUserInAuth0.entities.Template;
import com.oidccall.createUserInAuth0.repository.RestaurateurRepository;
import com.oidccall.createUserInAuth0.repository.SeatingCapacityRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class TemplateSCService {

	private final RestaurateurRepository restaurateurRepository;
	private final SeatingCapacityRepository seatingCapacityRepository;
	private final Function<Template, Set<String>> dinnerTableNumbersFunction = Template::getDinnerTableNumbers;
	private final Function<Template, Set<String>> lunchTableNumbersFunction = Template::getLunchTableNumbers;

	public List<Map<Long, List<SeatingCapacityMax>>> serviceCapacityImpl(long userId) {
		List<Map<Long, List<SeatingCapacityMax>>> lunchAndDinnerSC = new ArrayList<>();
		Restaurateur restaurateur = restaurateurRepository.findByUsersId(userId).orElseThrow();

		var mapLunch = convertToListSC(restaurateur, lunchTableNumbersFunction);
		var mapDinner = convertToListSC(restaurateur, dinnerTableNumbersFunction);
		lunchAndDinnerSC.add(mapLunch);
		lunchAndDinnerSC.add(mapDinner);
		return lunchAndDinnerSC;
	}

	private Map<Long, List<SeatingCapacityMax>> convertToListSC(Restaurateur restaurateur,
			Function<Template, Set<String>> getTableNumbersFunction) {
		List<Template> templates = restaurateur.getTemplates();
		Map<Long, List<SeatingCapacityMax>> mapResult = new HashMap<>();
		templates.forEach(template -> {
			Map.Entry<Long, List<SeatingCapacityMax>> convert = convertToSC(restaurateur, template, getTableNumbersFunction);
			mapResult.put(convert.getKey(), convert.getValue());
		});
		return mapResult;
	}

	private Map.Entry<Long, List<SeatingCapacityMax>> convertToSC(Restaurateur restaurateur, Template template,
			Function<Template, Set<String>> dinnerTableNumbersFunction) {
		Set<String> tableNumbers = dinnerTableNumbersFunction.apply(template);
		var seatingCapacity =
				this.seatingCapacityRepository.findAllByRestaurateurAndTableNumberIn(restaurateur, tableNumbers);
		var seatingCapacityMaxes = SeatingCapacityMaxMapper.mapToSeatingCapacityMax(seatingCapacity);
		return Map.entry(template.getId(), seatingCapacityMaxes);
	}

}
