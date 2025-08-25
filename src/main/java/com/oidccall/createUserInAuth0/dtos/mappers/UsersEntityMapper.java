package com.oidccall.createUserInAuth0.dtos.mappers;

import com.oidccall.createUserInAuth0.dtos.front.FrontUserToCreateDto;
import com.oidccall.createUserInAuth0.entities.Users;
import com.oidccall.dtos.feign.ResponseAuthApiV2UsersDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

public class UsersEntityMapper {

  private static final ModelMapper modelMapper;

  static {
    modelMapper = new ModelMapper();
    UsersEntityMapper.configureMappings();
  }

  private static void configureMappings() {
    TypeMap<ResponseAuthApiV2UsersDto, Users> typeMap = modelMapper.createTypeMap(ResponseAuthApiV2UsersDto.class, Users.class);

    typeMap.addMappings(mapper -> {
      mapper.map(ResponseAuthApiV2UsersDto::getUserId, Users::setAuth0UserId);
    });
    typeMap.setPostConverter(ctx -> {
      ResponseAuthApiV2UsersDto source = ctx.getSource();
      Users destination = ctx.getDestination();
      destination.setGender(GenderMapper.toDomain(source.getUser_metadata().getGender()));
      return destination;
    });

  }

  public static Users mapToUsersEntity(ResponseAuthApiV2UsersDto responseAuthApiV2UsersDto, FrontUserToCreateDto userFromFront) {
    Users users = modelMapper.map(responseAuthApiV2UsersDto, Users.class);
    users.setGender(GenderMapper.toDomain(userFromFront.gender()));
    return users;
  }

  public static Users mapToUsersEntity(ResponseAuthApiV2UsersDto responseAuthApiV2UsersDto) {
    return modelMapper.map(responseAuthApiV2UsersDto, Users.class);
  }

}
