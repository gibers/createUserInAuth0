package com.oidccall.createUserInAuth0.config;

import com.oidccall.createUserInAuth0.feignCalls.exceptions.CreateUserErrorDecoder;
import com.oidccall.createUserInAuth0.feignCalls.exceptions.GetUserErrorDecoder;
import com.oidccall.createUserInAuth0.feignCalls.exceptions.RoutingErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfiguration {

    @Bean
    public ErrorDecoder errorDecoder(CreateUserErrorDecoder createUserErrorDecoder,
                                     GetUserErrorDecoder getUserErrorDecoder) {
        return new RoutingErrorDecoder(createUserErrorDecoder, getUserErrorDecoder);
    }

}
