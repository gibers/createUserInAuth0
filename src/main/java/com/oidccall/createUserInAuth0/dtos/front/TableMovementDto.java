package com.oidccall.createUserInAuth0.dtos.front;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TableMovementDto(

		@NotNull
		long templateId,

		@NotNull
		@Size(max = 20)
		String tableId,

		@NotNull
		ServiceTypeDto serviceTypeDto

) {}
