package com.oidccall.createUserInAuth0.dtos.mappers;

import java.util.List;

import com.oidccall.createUserInAuth0.entities.DinnerServiceCapacity;
import com.oidccall.createUserInAuth0.entities.LunchServiceCapacity;
import com.oidccall.createUserInAuth0.entities.Template;
import com.oidccall.createUserInAuth0.entities.dtos.TemplateDto;

public final class TemplateEntityMapper {

    private TemplateEntityMapper() {
    }

    public static TemplateDto mapToTemplateDto(
            Template template,
            List<LunchServiceCapacity> lunchServiceCapacity,
            List<DinnerServiceCapacity> dinnerServiceCapacity
    ) {
        return TemplateDto.builder()
                .template(template)
                .lunchServiceCapacity(lunchServiceCapacity)
                .dinnerServiceCapacity(dinnerServiceCapacity)
                .build();
    }
}
