package com.oidccall.createUserInAuth0.controllers;

import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oidccall.createUserInAuth0.dtos.front.SeatingCapacityMax;
import com.oidccall.createUserInAuth0.entities.Users;
import com.oidccall.createUserInAuth0.exceptions.UnauthorizedUserAccessException;
import com.oidccall.createUserInAuth0.implementation.configuration.UpsertTableMaxCapacityService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/configuration")
@Slf4j
public class ConfigurationController {

  private final UpsertTableMaxCapacityService upsertTableMaxCapacityService;

  @PostMapping("/tableMax")
  public void upsertTableMaxCapacity(
      Authentication authentication,
      @RequestBody List<SeatingCapacityMax> seatingCapacityMax
  ) {
    var usersDto = authentication.getDetails();
    Object principal = authentication.getPrincipal();
    Users users = (Users) authentication.getDetails();
    log.debug("usersDto.getDetails(): {}", ReflectionToStringBuilder.toString(usersDto, ToStringStyle.JSON_STYLE));
    log.debug("usersDto.getPrincipal(): {}", ReflectionToStringBuilder.toString(principal, ToStringStyle.JSON_STYLE));

    this.upsertTableMaxCapacityService.upsertTableMaxCapacityImpl(users, seatingCapacityMax);
  }

  @GetMapping("/tableMax/{userId}")
  public List<SeatingCapacityMax> seatingCapacityFromUserId(Authentication authentication, @PathVariable String userId) {
    Users users = (Users) authentication.getDetails();
    if (!userId.equals(authentication.getName())) {
      throw new UnauthorizedUserAccessException("User " + userId + " does not correspond to the authorized user ");
    }
    return this.upsertTableMaxCapacityService.seatingCapacityFromUserIdImpl(users.getId());
  }

}
