package com.oidccall.createUserInAuth0.filters;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.oidccall.createUserInAuth0.entities.Users;
import com.oidccall.createUserInAuth0.exceptions.ErrorsEnum;
import com.oidccall.createUserInAuth0.repository.UsersRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoadUserInSecurityContext extends OncePerRequestFilter {

  private final UsersRepository usersRepository;

  public LoadUserInSecurityContext(UsersRepository usersRepository) {
    this.usersRepository = usersRepository;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !(request.getServletPath().startsWith("/configuration") || request.getServletPath().startsWith("/template"));
  }

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) {
      filterChain.doFilter(request, response);
      return;
    }

    Optional<Users> byUserId = usersRepository.findByAuth0UserId(jwtAuthentication.getName());
    byUserId.ifPresentOrElse(users -> {
      jwtAuthentication.setDetails(users);
      log.debug("User found in BDD users: {}", jwtAuthentication.getName());
    }, () -> {
      log.error("{}: {}", ErrorsEnum.E_1001.getOriginaErrorMessage(), jwtAuthentication.getName());
      throw new RuntimeException("User not found: " + jwtAuthentication.getName());
    });
    filterChain.doFilter(request, response);
  }

}
