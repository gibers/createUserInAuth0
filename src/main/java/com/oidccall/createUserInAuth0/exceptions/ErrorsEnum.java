package com.oidccall.createUserInAuth0.exceptions;

import lombok.Getter;

@Getter
public enum ErrorsEnum {

  E_1000("error from oauth0"),
  E_1001("token is valid, but the related user does not exist in the database Users"),
  E_1002("the user with id: %s no longer exists in the database Users")
  ;

  private final String originaErrorMessage;

  ErrorsEnum(String originaErrorMessage) {
    this.originaErrorMessage = originaErrorMessage;
  }

}
