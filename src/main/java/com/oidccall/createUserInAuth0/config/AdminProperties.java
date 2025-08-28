package com.oidccall.createUserInAuth0.config;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Value
@ConfigurationProperties(prefix = "admin")
public class AdminProperties {

  String userId;

}
