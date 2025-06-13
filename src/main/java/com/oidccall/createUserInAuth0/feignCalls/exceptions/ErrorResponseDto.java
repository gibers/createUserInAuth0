package com.oidccall.createUserInAuth0.feignCalls.exceptions;

public record ErrorResponseDto(
    int statusCode,
    String error,
    String message,
    String errorCode
) {}