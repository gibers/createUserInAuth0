package com.oidccall.createUserInAuth0.implementation.template;

import java.util.List;

import org.springframework.stereotype.Service;

import com.oidccall.createUserInAuth0.dtos.front.TemplateIdNameDto;
import com.oidccall.createUserInAuth0.dtos.front.TemplateWithoutServiceCapacityDto;
import com.oidccall.createUserInAuth0.dtos.mappers.TemplateEntityMapper;
import com.oidccall.createUserInAuth0.entities.Restaurateur;
import com.oidccall.createUserInAuth0.entities.Template;
import com.oidccall.createUserInAuth0.implementation.template.utils.TemplateUtilsComputeName;
import com.oidccall.createUserInAuth0.repository.RestaurateurRepository;
import com.oidccall.createUserInAuth0.repository.TemplateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class TemplateService {

	private final RestaurateurRepository restaurateurRepository;
	private final TemplateRepository templateRepository;
	private final TemplateServiceError templateServiceError;

	public List<TemplateWithoutServiceCapacityDto> getAllTemplateImpl(long userId) {
		Restaurateur restaurateur = restaurateurRepository.findByUsersId(userId).orElseThrow();
		List<Template> templates = restaurateur.getTemplates();
		return TemplateEntityMapper.mapToTemplateWithoutSCDto(templates);
	}

	public void changeTemplateNameImpl(long userId, TemplateIdNameDto templateIdNameDto) {
		Restaurateur restaurateur = restaurateurRepository.findByUsersId(userId).orElseThrow();
		templateServiceError.checkTemplateNameExist(restaurateur, templateIdNameDto.name());
		saveTemplateNameInDB(restaurateur, templateIdNameDto);
	}

	public TemplateWithoutServiceCapacityDto addTemplateImpl(long userId) {
		Restaurateur restaurateur = restaurateurRepository.findByUsersId(userId).orElseThrow();
		List<String> listTemplateName = restaurateur.getTemplates().stream().map(Template::getName).toList();
		String randomTemplateName = TemplateUtilsComputeName.computeName(listTemplateName);
		Template.TemplateBuilder builder = Template.builder();
		Template newTemplate = builder.restaurateur(restaurateur).name(randomTemplateName).build();
		Template templateSaved = this.templateRepository.save(newTemplate);
		return TemplateEntityMapper.mapToTemplateWithoutSCDto(templateSaved);
	}

	private void saveTemplateNameInDB(Restaurateur restaurateur, TemplateIdNameDto templateIdNameDto) {
		Template templateFromDB = getTemplateFromDB(restaurateur, templateIdNameDto.templateId());
		templateFromDB.setName(templateIdNameDto.name());
		this.templateRepository.save(templateFromDB);
	}

	private Template getTemplateFromDB(Restaurateur restaurateur, Long templateId) {
		return restaurateur.getTemplates().stream()
				.filter(template -> template.getId().equals(templateId))
				.findFirst()
				.orElseThrow();
	}

}
