package com.oidccall.createUserInAuth0.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationErrorHandler authenticationErrorHandler;

    @Bean
    public SecurityFilterChain httpSecurity(final HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(authz ->
                authz
                    .requestMatchers(HttpMethod.GET, "/api/hello", "/api/token").permitAll()
                    .requestMatchers(HttpMethod.PUT, "/users/create").permitAll()
                    .anyRequest().authenticated())
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults())
                .authenticationEntryPoint(authenticationErrorHandler))
            .build();
    }
}

