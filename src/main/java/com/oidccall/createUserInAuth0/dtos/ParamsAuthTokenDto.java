package com.oidccall.createUserInAuth0.dtos;

public record ParamsAuthTokenDto(String client_id, String client_secret, String audience, String grant_type) {

}
