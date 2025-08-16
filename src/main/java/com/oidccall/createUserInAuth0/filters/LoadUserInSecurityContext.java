package com.oidccall.createUserInAuth0.filters;

import com.oidccall.createUserInAuth0.entities.Users;
import com.oidccall.createUserInAuth0.entities.dtos.UsersDto;
import com.oidccall.createUserInAuth0.exceptions.ErrorsEnum;
import com.oidccall.createUserInAuth0.repository.UsersRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

//@Component
@Slf4j
public class LoadUserInSecurityContext extends OncePerRequestFilter {

  private final UsersRepository usersRepository;

  public LoadUserInSecurityContext(UsersRepository usersRepository) {
    this.usersRepository = usersRepository;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      Optional<Users> byUserId = usersRepository.findByAuth0UserId(authentication.getName());
      byUserId.ifPresentOrElse(users -> {
        UsersDto usersDto = UsersDto.fromEntity(users);
        authentication.setDetails(usersDto);
        log.debug("User found in BDD users: {}", authentication.getName());
      }, () -> {
        log.error("{}: {}", ErrorsEnum.E_1001.getOriginaErrorMessage(), authentication.getName());
        throw new RuntimeException("User not found: " + authentication.getName());
      });
    }
    filterChain.doFilter(request, response);
  }

}

