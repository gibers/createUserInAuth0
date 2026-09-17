package com.oidccall.createUserInAuth0.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Getter
@Slf4j
@ConfigurationProperties(prefix = "mavariable")
public class MaVariableProperties {

    private String profile;

	@PostConstruct
    public void init() {
        log.debug("Propriété mavariable.profile au démarrage : {}", profile);
    }
}
