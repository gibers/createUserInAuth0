package com.oidccall.createUserInAuth0.dtos;

import lombok.With;

public record ParamsAuthApiV2UpdatePhoneUsers(
  @With String phone_number
) implements IParamsAuthApiV2UpdateUsers {

}
