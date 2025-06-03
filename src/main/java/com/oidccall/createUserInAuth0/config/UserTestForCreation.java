package com.oidccall.createUserInAuth0.config;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Value
@ConfigurationProperties(prefix = "usertestforcreation")
public class UserTestForCreation {

  String email;
  String password;
  String connection;

}
