package com.oidccall.createUserInAuth0.dtos.front;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TemplateIdNameDto(
		Long templateId,
		@NotNull @Size(max = 65) String name
) {
}
