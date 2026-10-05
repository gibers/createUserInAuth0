package com.oidccall.createUserInAuth0.entities.dtos;

import java.util.List;

import com.oidccall.createUserInAuth0.entities.DinnerServiceCapacity;
import com.oidccall.createUserInAuth0.entities.LunchServiceCapacity;
import com.oidccall.createUserInAuth0.entities.Template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDto {

    private Template template;
    private List<LunchServiceCapacity> lunchServiceCapacity;
    private List<DinnerServiceCapacity> dinnerServiceCapacity;
}
