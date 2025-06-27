package com.oidccall.createUserInAuth0.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
public class ResponseAuthApiV2UsersDto {
    @JsonProperty("created_at")
    private Instant createdAt;
    
    private String email;
    
    @JsonProperty("email_verified")
    private boolean emailVerified;
    
    private List<Identity> identities;
    
    private String name;
    
    private String nickname;
    
    private String picture;
    
    @JsonProperty("updated_at")
    private Instant updatedAt;
    
    @JsonProperty("user_id")
    private String userId;
    
    @Data
    public static class Identity {
        private String connection;
        
        @JsonProperty("user_id")
        private String userId;
        
        private String provider;
        
        @JsonProperty("isSocial")
        private boolean isSocial;
    }

    private String phone_number;
    private String last_ip;
    private String last_login;
    private long logins_count;

}
