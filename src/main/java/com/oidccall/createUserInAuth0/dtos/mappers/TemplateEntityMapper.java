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
        listTemplate.forEach(t -> {
            TemplateWithoutServiceCapacityDto build = TemplateWithoutServiceCapacityDto.builder()
                    .id(t.getId())
                    .name(t.getName())
                    .active(t.isActive())
                    .monday(t.isMonday())
                    .tuesday(t.isTuesday())
                    .wednesday(t.isWednesday())
                    .thursday(t.isThursday())
                    .friday(t.isFriday())
                    .saturday(t.isSaturday())
                    .sunday(t.isSunday())
                    .dataSolo(t.getDataSolo())
                    .validFrom(t.getValidFrom())
                    .comment(t.getComment())
                    .build();
            listResult.add(build);
        });
        return listResult;
    }

}
