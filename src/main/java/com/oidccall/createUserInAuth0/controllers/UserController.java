package com.oidccall.createUserInAuth0.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oidccall.createUserInAuth0.dtos.ResponseAuthApiV2UsersDto;
import com.oidccall.createUserInAuth0.dtos.front.FrontUserToCreateDto;
import com.oidccall.createUserInAuth0.entities.dtos.UsersDto;
import com.oidccall.createUserInAuth0.exceptions.UnauthorizedUserAccessException;
import com.oidccall.createUserInAuth0.feignCalls.ApiV2UsersRequest;
import com.oidccall.createUserInAuth0.implementation.UserImplementation;
import com.oidccall.createUserInAuth0.mock.ConvertFromResourceToObj;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {

  @NonNull
  private final UserImplementation userImplementation;
  @NonNull
  final private ApiV2UsersRequest apiV2UsersRequest;

  // ReceiveController_createUser.md
  @PostMapping("/create")
  public ResponseAuthApiV2UsersDto createUser(@Valid @RequestBody FrontUserToCreateDto userToCreateDto) throws JsonProcessingException {
//    ResponseAuthApiV2UsersDto userCreated = this.userImplementation.createUserInAuth0(userToCreateDto);
    ResponseAuthApiV2UsersDto responseAuthApiV2UsersDto = loadMockResponseAuthApiV2UsersDto();
    log.debug("userCreated: --- ");
    return responseAuthApiV2UsersDto;
  }

  @GetMapping("/{userId}")
  public ResponseAuthApiV2UsersDto getUser(Authentication authentication, @PathVariable String userId)
      throws ResponseStatusException {
    if (!userId.equals(authentication.getName())) {
      throw new UnauthorizedUserAccessException("User " + userId + " does not correspond to the authorized user ");
    }

//    JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
//    String sub = jwtAuthenticationToken.getTokenAttributes().get("sub").toString();
//
//    Jwt principal = (Jwt) authentication.getPrincipal();
    UsersDto usersDto = (UsersDto) authentication.getDetails();
    log.debug("usersDto: {}", usersDto);
//    String id = principal.getId();
//    String userIdFromJwtAuthenticationToken = principal.getClaims().get("sub").toString();
    return this.apiV2UsersRequest.getUserApiV2Users(userId);
  }

//  @GetMapping("/tt/{userIdT}")
//  public ResponseAuthApiV2UsersDto getUserT(Authentication authentication, @PathVariable String userId) {
//    if (!userId.equals(authentication.getName())) {
//      throw new UnauthorizedUserAccessException("User " + userId + " does not correspond to the authorized user ");
//    }
//    return this.userImplementation.getUserFromAuth0(userId);
//  }

  @DeleteMapping("/{userId}")
  public void deleteUser(Authentication authentication, @PathVariable String userId) {
    log.debug("User deleted: {}", authentication);
    if (!userId.equals(authentication.getName())) {
      throw new UnauthorizedUserAccessException("User " + userId + " does not correspond to the authorized user ");
    }
    this.userImplementation.deleteUserInAuth0(userId);
  }

  // -------------------------------------

  private ResponseAuthApiV2UsersDto loadMockResponseAuthApiV2UsersDto() {
    return ConvertFromResourceToObj.getObjectFromResource("mockObjects/ResponseAuthApiV2UsersDto1.json", ResponseAuthApiV2UsersDto.class);
  }

}
