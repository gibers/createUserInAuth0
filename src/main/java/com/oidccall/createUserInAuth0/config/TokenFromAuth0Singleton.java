//package com.oidccall.createUserInAuth0.config;
//
//import com.oidccall.createUserInAuth0.dtos.ResponseAuthTokenDto;
//import com.oidccall.createUserInAuth0.feignCalls.AuthTokenRequest;
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.interfaces.DecodedJWT;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.Date;
//
//@Slf4j
//public class TokenFromAuth0Singleton {
//    private static volatile TokenFromAuth0Singleton instance;
//    private volatile ResponseAuthTokenDto token;
//    private final AuthTokenRequest authTokenRequest;
//
//    private TokenFromAuth0Singleton(AuthTokenRequest authTokenRequest) {
//        this.authTokenRequest = authTokenRequest;
//    }
//
//    public static TokenFromAuth0Singleton getInstance(AuthTokenRequest authTokenRequest) {
//        TokenFromAuth0Singleton result = instance;
//        if (result == null) {
//            synchronized (TokenFromAuth0Singleton.class) {
//                result = instance;
//                if (result == null) {
//                    instance = result = new TokenFromAuth0Singleton(authTokenRequest);
//                }
//            }
//        }
//        return result;
//    }
//
//    public ResponseAuthTokenDto getFullToken() {
//        if (this.token == null || this.isTokenExpired()) {
//            synchronized (this) {
//                if (this.token == null || this.isTokenExpired()) {
//                    this.token = this.authTokenRequest.requestToken();
//                }
//            }
//        }
//        return token;
//    }
//
//    private boolean isTokenExpired() {
//        String accessToken = this.token.getAccess_token();
//        DecodedJWT jwt = JWT.decode(accessToken);
//        Date expiryDate = jwt.getExpiresAt();
//        long _10minutes = 1000 * 60 * 10;
//        long rajout = new Date().getTime() + _10minutes;
//        return (expiryDate == null) || expiryDate.before(new Date(rajout));
//    }
//
//}
