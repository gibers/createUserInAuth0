package com.oidccall.createUserInAuth0.implementation;
import com.oidccall.createUserInAuth0.dtos.UserMetada;
import org.apache.commons.lang3.StringUtils;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public class UtilsUserFonctions {

  public enum ChangeType {
    CHANGE_EMAIL,
    CHANGE_PHONENUMBER,
    CHANGE_DATA,
    CHANGE_GENDER
  }

  public static final UnaryOperator<UserMetada> transformUserMetada = (userMetada) ->
    (userMetada == null) ? new UserMetada() : userMetada;

  public static final BiFunction<String, String, Boolean> isEqualsIgnoreCaseAndNull = (s1, s2) ->
    StringUtils.compareIgnoreCase(
      StringUtils.defaultIfBlank(s1, ""),
      StringUtils.defaultIfBlank(s2, "").trim()) == 0;

}
