package com.oidccall.createUserInAuth0.implementation.template.utils;

import java.util.List;

public class TemplateUtilsComputeName {

	public static String computeName(List<String> listTemplateName) {
		var ref = new Object() {
			String proposition = "Template's name";
		};
		while (listTemplateName.stream().anyMatch(x -> x.equals(ref.proposition))) {
			ref.proposition = ref.proposition + "1";
		}
		return ref.proposition;
	}

}
