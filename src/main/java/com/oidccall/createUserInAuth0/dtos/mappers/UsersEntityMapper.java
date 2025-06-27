package com.oidccall.createUserInAuth0.dtos.mappers;

import com.oidccall.createUserInAuth0.dtos.ResponseAuthApiV2UsersDto;
import com.oidccall.createUserInAuth0.dtos.front.FrontUserToCreateDto;
import com.oidccall.createUserInAuth0.entities.Users;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

public class UsersEntityMapper {

    private static final ModelMapper modelMapper;

    static {
        modelMapper = new ModelMapper();
        UsersEntityMapper.configureMappings();
    }

    static void configureMappings() {
        TypeMap<ResponseAuthApiV2UsersDto, Users> typeMap = modelMapper.createTypeMap(ResponseAuthApiV2UsersDto.class, Users.class);
        typeMap.addMappings(mapper -> {
            mapper.map(ResponseAuthApiV2UsersDto::getUserId, Users::setAuth0UserId);
            mapper.map(ResponseAuthApiV2UsersDto::getName, Users::setUsername);
        });
    }

    public static Users mapToUsersEntity(ResponseAuthApiV2UsersDto responseAuthApiV2UsersDto, FrontUserToCreateDto userFromFront) {
        Users users = modelMapper.map(responseAuthApiV2UsersDto, Users.class);
        users.setGender(userFromFront.gender());
        return users;
    }

}
