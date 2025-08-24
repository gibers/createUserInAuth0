package com.oidccall.createUserInAuth0.dtos;

import lombok.With;

public record ParamsAuthApiV2UpdateVerifiedEmail(
  @With Boolean email_verified
) implements IParamsAuthApiV2UpdateUsers {}
