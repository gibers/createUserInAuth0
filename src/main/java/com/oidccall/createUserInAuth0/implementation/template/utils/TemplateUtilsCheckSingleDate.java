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

	public static void putAllDaysAtFalse(Template templateFromDB) {
		templateFromDB.setMonday(false);
		templateFromDB.setTuesday(false);
		templateFromDB.setWednesday(false);
		templateFromDB.setThursday(false);
		templateFromDB.setFriday(false);
		templateFromDB.setSaturday(false);
		templateFromDB.setSunday(false);
	}

}
