package com.oidccall.createUserInAuth0.controllers;

import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oidccall.createUserInAuth0.entities.Users;
import com.oidccall.createUserInAuth0.exceptions.UnauthorizedUserAccessException;
import com.oidccall.createUserInAuth0.implementation.configuration.upsertTableMaxCapacityService;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/configuration")
@Slf4j
public class ConfigurationController {

  private final upsertTableMaxCapacityService upsertTableMaxCapacityService;

  @PostMapping("/tableMax")
  public void upsertTableMaxCapacity(Authentication authentication, @RequestBody Map<String, Integer> tableMaxCapacity) {
    var usersDto = authentication.getDetails();
    Object principal = authentication.getPrincipal();
    Users users = (Users) authentication.getDetails();
    log.debug("usersDto.getDetails(): {}", ReflectionToStringBuilder.toString(usersDto, ToStringStyle.JSON_STYLE));
    log.debug("usersDto.getPrincipal(): {}", ReflectionToStringBuilder.toString(principal, ToStringStyle.JSON_STYLE));

    this.upsertTableMaxCapacityService.upsertTableMaxCapacityImpl(users, tableMaxCapacity);
  }

  @GetMapping("/tableMax/{userId}")
  public Map<@NotNull @Size(max = 20) String, @NotNull @Min(0) @Max(22) Integer> seatingCapacityFromUserId(Authentication authentication, @PathVariable String userId) {
    Users users = (Users) authentication.getDetails();
    if (!userId.equals(authentication.getName())) {
      throw new UnauthorizedUserAccessException("User " + userId + " does not correspond to the authorized user ");
    }
    return this.upsertTableMaxCapacityService.seatingCapacityFromUserIdImpl(users.getId());
  }

}

