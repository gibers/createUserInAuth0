package com.oidccall.createUserInAuth0.dtos.mappers;

import com.oidccall.createUserInAuth0.enums.GenderEnum;
import com.oidccall.dtos.enums.GenderEnumDto;

public final class GenderMapper {

  private GenderMapper() {}

  public static GenderEnum toDomain(GenderEnumDto external) {
    if (external == null) return null;
    return switch (external) {
      case MALE -> GenderEnum.MALE;
      case FEMALE -> GenderEnum.FEMALE;
      default -> GenderEnum.OTHER;
    };
  }

  public static GenderEnumDto toExternal(GenderEnum domain) {
    if (domain == null) return null;
    return switch (domain) {
      case MALE -> GenderEnumDto.MALE;
      case FEMALE -> GenderEnumDto.FEMALE;
      default -> GenderEnumDto.OTHER;
    };
  }
}