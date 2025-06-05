package com.oidccall.createUserInAuth0.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.oidccall.createUserInAuth0.dtos.ResponseAuthTokenDto;
import com.oidccall.createUserInAuth0.feignCalls.AuthTokenRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class TokenFromAuth0 {
    private volatile ResponseAuthTokenDto token;
    private final AuthTokenRequest authTokenRequest;

    public TokenFromAuth0(AuthTokenRequest authTokenRequest) {
        this.authTokenRequest = authTokenRequest;
    }

    public synchronized ResponseAuthTokenDto getFullToken() {
        if (this.token == null || this.isTokenExpired()) {
            this.token = this.authTokenRequest.requestToken();
        }
        return token;
    }

    private boolean isTokenExpired() {
        String accessToken = this.token.getAccess_token();
        DecodedJWT jwt = JWT.decode(accessToken);
        Date expiryDate = jwt.getExpiresAt();
        long _10minutes = 1000 * 60 * 10;
        long rajout = new Date().getTime() + _10minutes;
        return (expiryDate == null) || expiryDate.before(new Date(rajout));
    }
}
