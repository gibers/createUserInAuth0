package com.oidccall.createUserInAuth0.feignCalls;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.oidccall.createUserInAuth0.config.Auth0Properties;
import com.oidccall.createUserInAuth0.config.UserTestForCreation;
import com.oidccall.createUserInAuth0.dtos.ParamsAuthApiV2UsersDto;
import com.oidccall.createUserInAuth0.dtos.ResponseAuthApiV2UsersDto;
import com.oidccall.createUserInAuth0.interfaces.GetTokenWithFeign;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ApiV2UsersRequest {

  private final GetTokenWithFeign getTokenWithFeign;
  private final UserTestForCreation userTestForCreation;

  public ApiV2UsersRequest(Auth0Properties auth0Properties, UserTestForCreation userTestForCreation) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    this.userTestForCreation = userTestForCreation;
    this.getTokenWithFeign = Feign.builder()
        .encoder(new JacksonEncoder(mapper))
        .decoder(new JacksonDecoder(mapper))
        .target(GetTokenWithFeign.class,"https://"+auth0Properties.getDomain());
  }

  public ResponseAuthApiV2UsersDto createUserInAuth0(String token) {
    ParamsAuthApiV2UsersDto paramsAuthApiV2UsersDto = new ParamsAuthApiV2UsersDto(
        userTestForCreation.getEmail(), userTestForCreation.getConnection(), userTestForCreation.getPassword());
    return getTokenWithFeign.postApiV2Users(token, paramsAuthApiV2UsersDto);
  }

}
