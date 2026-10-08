package com.oidccall.createUserInAuth0.implementation.template;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.oidccall.createUserInAuth0.entities.Restaurateur;
import com.oidccall.createUserInAuth0.entities.Template;
import com.oidccall.createUserInAuth0.exceptions.ErrorsEnum;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TemplateServiceError {

	public static final int MAX_TEMPLATES = 5;

	void checkTemplateNameExist(Restaurateur restaurateur, @NotNull @Size(max = 65) String templateName) {
		boolean nameExistInDB = restaurateur.getTemplates().stream()
				.anyMatch(template -> template.getName().equals(templateName));
		if (!nameExistInDB) {
			return;
		}
		Template existingTemplateFromDB = restaurateur.getTemplates().stream()
				.filter(template -> template.getName().equals(templateName))
				.findFirst()
				.orElseThrow();
		log.error(ErrorsEnum.E_1100.getPrivateErrorMessage(), existingTemplateFromDB);
		throw new DataIntegrityViolationException(ErrorsEnum.E_1100.getPublicErrorMessage());
	}

}
