package com.oidccall.createUserInAuth0.feignCalls.exceptions;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RoutingErrorDecoder implements ErrorDecoder {
    private final Map<String, ErrorDecoder> errorDecoders;

    public RoutingErrorDecoder(CreateUserErrorDecoder createUserErrorDecoder,
                               GetUserErrorDecoder getUserErrorDecoder) {
      this.errorDecoders = new HashMap<>();
        errorDecoders.put("GetTokenWithFeign#postOauthToken(ParamsAuthTokenDto)", createUserErrorDecoder);
        errorDecoders.put("GetTokenWithFeign#createUserApiV2Users(String,ParamsAuthApiV2UsersDto)", createUserErrorDecoder);
        errorDecoders.put("GetTokenWithFeign#getUserApiV2Users(String,String)", getUserErrorDecoder);
        errorDecoders.put("GetTokenWithFeign#deleteUserApiV2Users(String,String)", getUserErrorDecoder);
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        ErrorDecoder errorDecoder = errorDecoders.getOrDefault(methodKey, new Default());
        return errorDecoder.decode(methodKey, response);
    }

}
