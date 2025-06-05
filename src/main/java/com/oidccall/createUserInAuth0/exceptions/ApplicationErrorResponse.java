package com.oidccall.createUserInAuth0.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApplicationErrorResponse {
    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
}
