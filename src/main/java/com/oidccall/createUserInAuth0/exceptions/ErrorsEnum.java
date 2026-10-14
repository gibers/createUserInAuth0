package com.oidccall.createUserInAuth0.exceptions;

public enum ErrorsEnum {

  E_1000("error from oauth0", "error from oauth0"),
  E_1001("token is valid, but the related user does not exist in the database Users", "token is valid, but the related user does not exist in the database Users"),
  E_1002("the user with id: {} no longer exists in the database Users", "the user with id: {} no longer exists in the database Users"),
  E_1003("the user with id: {} with deleted false, no longer exists in the database Users", "the user with id: {} with deleted false, no longer exists in the database Users"),

  E_1100("error in the system", "template name already exist: {} "),
  E_1101("error in the system", "Day conflict: {} is already selected"),
  E_1102("error in the system", "no active template found with id: {}")
  ;

  private final String publicErrorMessage;
  private final String privateErrorMessage;

  ErrorsEnum(String publicErrorMessage, String privateErrorMessage) {
    this.publicErrorMessage = publicErrorMessage;
    this.privateErrorMessage = privateErrorMessage;
  }

  public String getPublicErrorMessage() {
    return name() + " " + publicErrorMessage;
  }

  public String getPrivateErrorMessage() {
    return name() + " " + privateErrorMessage;
  }

}
