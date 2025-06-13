package com.oidccall.createUserInAuth0;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oidccall.createUserInAuth0.feignCalls.exceptions.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;

@Slf4j
public class ExtractMessageInErrorFromAuth0 {

  @Test
  void t1() throws JsonProcessingException {
    String error = "{\"statusCode\":400,\"error\":\"Bad Request\",\"message\":\"Payload validation error: 'String is too short (0 chars), minimum 1' on property username (The user's username. Only valid if the connection requires a username). (also) Payload validation error: 'Object didn't pass validation for format strict-uri: ' on property picture (A URI pointing to the user's picture). (also) Payload validation error: 'String is too short (0 chars), minimum 1' on property nickname (The user's nickname). (also) Payload validation error: 'String is too short (0 chars), minimum 1' on property name (The user's full name). (also) Payload validation error: 'String is too short (0 chars), minimum 1' on property family_name (The user's family name(s)). (also) Payload validation error: 'String is too short (0 chars), minimum 1' on property given_name (The user's given name(s)). (also) Payload validation error: 'String does not match pattern ^\\\\+[0-9]{1,15}$: ' on property phone_number (The user's phone number (following the E.164 recommendation)).\",\"errorCode\":\"invalid_body\"}";

    ObjectMapper objectMapper = new ObjectMapper();
    ErrorResponseDto errorResponseDto = objectMapper.readValue(error, ErrorResponseDto.class);

    HttpStatusCode httpStatusCode = HttpStatusCode.valueOf(errorResponseDto.statusCode());
    log.info("error: {}", httpStatusCode);

  }

}
