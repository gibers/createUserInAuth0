package com.oidccall.createUserInAuth0.exceptions;

import lombok.Getter;

@Getter
public enum ErrorsEnum {

  E_1000("error from oauth0", "error from oauth0"),
  E_1001("token is valid, but the related user does not exist in the database Users", "token is valid, but the related user does not exist in the database Users"),
  E_1002("the user with id: %s no longer exists in the database Users", "the user with id: %s no longer exists in the database Users"),
  E_1003("the user with id: %s with deleted false, no longer exists in the database Users", "the user with id: %s with deleted false, no longer exists in the database Users"),
  E_1100("error in the system", "template name already exist: %s ")
  ;

  private final String publicErrorMessage;
  private final String privateErrorMessage;

  ErrorsEnum(String publicErrorMessage, String privateErrorMessage) {
    this.publicErrorMessage = publicErrorMessage;
    this.privateErrorMessage = privateErrorMessage;
  }

}
