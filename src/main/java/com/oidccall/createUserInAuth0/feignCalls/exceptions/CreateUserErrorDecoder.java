package com.oidccall.createUserInAuth0.feignCalls.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class CreateUserErrorDecoder implements ErrorDecoder {

  //  https://auth0.com/docs/api/management/v2/users/post-users
  @Override
  public Exception decode(String methodKey, Response response) {
    ErrorResponseDto errorResponseDto;
    try (InputStream inputStream = response.body().asInputStream()) {
      String errorJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
      ObjectMapper objectMapper = new ObjectMapper();
      errorResponseDto = objectMapper.readValue(errorJson, ErrorResponseDto.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    HttpStatusCode httpStatusCode = HttpStatusCode.valueOf(errorResponseDto.statusCode());
    return new ResponseStatusException(httpStatusCode, errorResponseDto.message());
  }

}
