package com.oidccall.createUserInAuth0.dtos.front;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SeatingCapacityMax(
  @NotNull
  @Size(max = 20)
  String name,

  @NotNull
  @Min(0)
  @Max(22)
  Integer number
) {}
