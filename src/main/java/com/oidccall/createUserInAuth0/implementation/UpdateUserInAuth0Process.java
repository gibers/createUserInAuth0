package com.oidccall.createUserInAuth0.implementation;

import com.oidccall.createUserInAuth0.dtos.front.SimpleUserData;
import com.oidccall.dtos.feign.ParamsAuthApiV2UpdatePhoneUsers;
import com.oidccall.dtos.feign.ParamsAuthApiV2UpdateUsers;
import com.oidccall.dtos.feign.ResponseAuthApiV2UsersDto;
import com.oidccall.dtos.feign.UserMetada;
import com.oidccall.feigncallslib.feignCalls.ApiV2UsersRequestLib;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
@Slf4j
public class UpdateUserInAuth0Process {

  private final ApiV2UsersRequestLib apiV2UsersRequest;
  private ResponseAuthApiV2UsersDto responseAuthApiV2UsersDto;
  private @Valid SimpleUserData simpleUserData;

  public void process(String userId, @Valid SimpleUserData simpleUserData) {
    this.simpleUserData = simpleUserData;
    // 1. get the user from auth0
    this.responseAuthApiV2UsersDto = this.apiV2UsersRequest.getUserApiV2Users(userId);
    // 2. looking for differences between the user in DB and the user in request
    ArrayList<UtilsUserFonctions.ChangeType> changeType = isEqualsBetweenUserInDBAndUserInRequest();
    this.updateUserInAuth0(changeType);
  }

  private void updateUserInAuth0(ArrayList<UtilsUserFonctions.ChangeType> changeType) {
    List<UtilsUserFonctions.ChangeType> collect = changeType.stream()
      .filter(x -> !changeType.contains(UtilsUserFonctions.ChangeType.CHANGE_DATA) || x != UtilsUserFonctions.ChangeType.CHANGE_GENDER)
      .toList();

    for (UtilsUserFonctions.ChangeType c: collect) {
      switch (c) {
        case CHANGE_EMAIL: {
          this.updateUsersEmailInAuth0();
          break;
        }
        case CHANGE_PHONENUMBER: {
          this.updateUsersPhoneNumberInAuth0();
          break;
        }
        case CHANGE_GENDER:
        case CHANGE_DATA: {
          this.updateUsersDataInAuth0();
          break;
        }
        default:
          break;
      }
    }
  }

  private void updateUsersDataInAuth0() {
    UserMetada userMetada = new UserMetada(this.simpleUserData.gender());
    ParamsAuthApiV2UpdateUsers p1 = ParamsAuthApiV2UpdateUsers.forData(this.simpleUserData.family_name()
      , this.simpleUserData.given_name()
      , this.simpleUserData.nickname(), userMetada
      , this.responseAuthApiV2UsersDto
    );
    this.apiV2UsersRequest.updateUsers(this.responseAuthApiV2UsersDto.getUserId(), p1);
  }

  private void updateUsersPhoneNumberInAuth0() {
    ParamsAuthApiV2UpdatePhoneUsers p1 = new ParamsAuthApiV2UpdatePhoneUsers(
      StringUtils.isBlank(this.simpleUserData.phone_number()) ? null : this.simpleUserData.phone_number().replace(" ", ""));
    this.apiV2UsersRequest.updateUsers(this.responseAuthApiV2UsersDto.getUserId(), p1);
  }

  private void updateUsersEmailInAuth0() {
    ParamsAuthApiV2UpdateUsers p1 = ParamsAuthApiV2UpdateUsers.forEmail(this.simpleUserData.email(), this.responseAuthApiV2UsersDto);
    this.apiV2UsersRequest.updateUsers(this.responseAuthApiV2UsersDto.getUserId(), p1);
  }

  private ArrayList<UtilsUserFonctions.ChangeType> isEqualsBetweenUserInDBAndUserInRequest() {
    var listChangeType = new ArrayList<UtilsUserFonctions.ChangeType>();
    boolean userInDBEqualsUserInRequest = this.responseAuthApiV2UsersDto.getEmail().equalsIgnoreCase(this.simpleUserData.email());
    if (!userInDBEqualsUserInRequest) {
      listChangeType.add(UtilsUserFonctions.ChangeType.CHANGE_EMAIL);
    }
    userInDBEqualsUserInRequest = UtilsUserFonctions.isEqualsIgnoreCaseAndNullPhoneNumber.apply(
      this.responseAuthApiV2UsersDto.getPhone_number(), this.simpleUserData.phone_number());
    if (!userInDBEqualsUserInRequest) {
      listChangeType.add(UtilsUserFonctions.ChangeType.CHANGE_PHONENUMBER);
    }
    userInDBEqualsUserInRequest = UtilsUserFonctions.isEqualsIgnoreCaseAndNull.apply(this.responseAuthApiV2UsersDto.getNickname(), this.simpleUserData.nickname());
    userInDBEqualsUserInRequest = userInDBEqualsUserInRequest &&
      UtilsUserFonctions.isEqualsIgnoreCaseAndNull.apply(this.responseAuthApiV2UsersDto.getFamily_name(), this.simpleUserData.family_name());
    userInDBEqualsUserInRequest = userInDBEqualsUserInRequest &&
      UtilsUserFonctions.isEqualsIgnoreCaseAndNull.apply(this.responseAuthApiV2UsersDto.getGiven_name(), this.simpleUserData.given_name());
    if (!userInDBEqualsUserInRequest) {
      listChangeType.add(UtilsUserFonctions.ChangeType.CHANGE_DATA);
    }
    var userMetadaTemp = UtilsUserFonctions.transformUserMetada.apply(this.responseAuthApiV2UsersDto.getUser_metadata());
    if (this.simpleUserData.gender() != userMetadaTemp.getGender()) {
      listChangeType.add(UtilsUserFonctions.ChangeType.CHANGE_GENDER);
    }
    return listChangeType;
  }

}
