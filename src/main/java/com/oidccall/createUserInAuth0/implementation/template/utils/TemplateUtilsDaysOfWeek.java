package com.oidccall.createUserInAuth0.implementation.template.utils;

import java.util.List;
import java.util.function.BiFunction;

import com.oidccall.createUserInAuth0.dtos.front.TemplateWithoutServiceCapacityDto;
import com.oidccall.createUserInAuth0.entities.Template;

public final class TemplateUtilsDaysOfWeek {

	private TemplateUtilsDaysOfWeek() {
	}

	public static boolean atLeastOneMondayIsTrue(List<Template> templates) {
		return templates.stream().anyMatch(Template::isMonday);
	}

	public static boolean atLeastOneTuesdayIsTrue(List<Template> templates) {
		return templates.stream().anyMatch(Template::isTuesday);
	}

	public static boolean atLeastOneWednesdayIsTrue(List<Template> templates) {
		return templates.stream().anyMatch(Template::isWednesday);
	}

	public static boolean atLeastOneThursdayIsTrue(List<Template> templates) {
		return templates.stream().anyMatch(Template::isThursday);
	}

	public static boolean atLeastOneFridayIsTrue(List<Template> templates) {
		return templates.stream().anyMatch(Template::isFriday);
	}

	public static boolean atLeastOneSaturdayIsTrue(List<Template> templates) {
		return templates.stream().anyMatch(Template::isSaturday);
	}

	public static boolean atLeastOneSundayIsTrue(List<Template> templates) {
		return templates.stream().anyMatch(Template::isSunday);
	}

	public static List<BiFunction<TemplateWithoutServiceCapacityDto, TemplateWithoutServiceCapacityDto, String>> bothDaysAreTrue() {
		return List.of(
				(dto1, dto2) -> dto1.isMonday() && dto2.isMonday() ? "Monday" : "",
				(dto1, dto2) -> dto1.isTuesday() && dto2.isTuesday() ? "Tuesday" : "",
				(dto1, dto2) -> dto1.isWednesday() && dto2.isWednesday() ? "Wednesday" : "",
				(dto1, dto2) -> dto1.isThursday() && dto2.isThursday() ? "Thursday" : "",
				(dto1, dto2) -> dto1.isFriday() && dto2.isFriday() ? "Friday" : "",
				(dto1, dto2) -> dto1.isSaturday() && dto2.isSaturday() ? "Saturday" : "",
				(dto1, dto2) -> dto1.isSunday() && dto2.isSunday() ? "Sunday" : "");
	}
}
