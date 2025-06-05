package com.oidccall.createUserInAuth0.feignCalls.exceptions;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CreateUserErrorDecoder implements ErrorDecoder {

//  https://auth0.com/docs/api/management/v2/users/post-users
    @Override
    public Exception decode(String methodKey, Response response) {
      return switch (response.status()) {
        case 400 -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Connection does not support user creation through the API. It must either be a database or passwordless connection");
        case 401 -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        case 403 -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient scope, expected any of: create:users");
        case 409 -> new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        case 429 -> new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests. Check the X-RateLimit-Limit, X-RateLimit-Remaining and X-RateLimit-Reset headers");
        default -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error");
      };
    }

}
