package com.oidccall.createUserInAuth0.config.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import com.oidccall.createUserInAuth0.filters.LoadUserInSecurityContext;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final LoadUserInSecurityContext loadUserInSecurityContext;

    @Bean
    public FilterRegistrationBean<LoadUserInSecurityContext>
    disableAutomaticRegistration() {
        FilterRegistrationBean<LoadUserInSecurityContext> registration =
                new FilterRegistrationBean<>(loadUserInSecurityContext);

        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public SecurityFilterChain httpSecurity(final HttpSecurity http) throws Exception {
        return http
                .addFilterAfter(loadUserInSecurityContext, BearerTokenAuthenticationFilter.class)
                .authorizeHttpRequests(authz ->
                        authz
                                .requestMatchers(HttpMethod.GET, "/api/hello", "/api/token").permitAll()
                                .requestMatchers(HttpMethod.POST, "/users/create").permitAll()
                                .requestMatchers("/error").permitAll()
                                .anyRequest().authenticated())
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(authenticationEntryPoint))
                .build();
    }

}

