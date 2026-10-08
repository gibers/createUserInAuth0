package com.oidccall.createUserInAuth0.dtos.mappers;

import java.util.ArrayList;
import java.util.List;

import com.oidccall.createUserInAuth0.dtos.front.TemplateWithoutServiceCapacityDto;
import com.oidccall.createUserInAuth0.entities.Template;

public final class TemplateEntityMapper {

    private TemplateEntityMapper() {
    }

    public static List<TemplateWithoutServiceCapacityDto> mapToTemplateWithoutSCDto(
            List<Template> listTemplate
    ) {
        List<TemplateWithoutServiceCapacityDto> listResult = new ArrayList<>();
        listTemplate.forEach(t -> listResult.add(mapToTemplateWithoutSCDto(t)));
        return listResult;
    }

    public static TemplateWithoutServiceCapacityDto mapToTemplateWithoutSCDto(Template template) {
        return TemplateWithoutServiceCapacityDto.builder()
                .id(template.getId())
                .name(template.getName())
                .active(template.isActive())
                .monday(template.isMonday())
                .tuesday(template.isTuesday())
                .wednesday(template.isWednesday())
                .thursday(template.isThursday())
                .friday(template.isFriday())
                .saturday(template.isSaturday())
                .sunday(template.isSunday())
                .dateSolo(template.getDateSolo())
                .validFrom(template.getValidFrom())
                .comment(template.getComment())
                .build();
    }

}
