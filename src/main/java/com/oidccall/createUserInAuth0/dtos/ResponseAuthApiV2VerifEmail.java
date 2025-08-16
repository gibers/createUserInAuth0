package com.oidccall.createUserInAuth0.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class ResponseAuthApiV2VerifEmail {
  private String type;
  private String status;
  @JsonProperty("created_at")
  private Instant createdAt;
  private String id;
}
