package com.oidccall.createUserInAuth0.implementation.template.utils;

import com.oidccall.createUserInAuth0.entities.Template;

public class TemplateUtilsCheckSingleDate {

	public static boolean atLeastOneDayOfWeekIsTrue(Template templateFromDB) {
		return templateFromDB.isMonday()
				|| templateFromDB.isTuesday()
				|| templateFromDB.isWednesday()
				|| templateFromDB.isThursday()
				|| templateFromDB.isFriday()
				|| templateFromDB.isSaturday()
				|| templateFromDB.isSunday();
	}

}
