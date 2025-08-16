package com.oidccall.createUserInAuth0.feignCalls;

import com.oidccall.createUserInAuth0.config.TokenFromAuth0;
import com.oidccall.createUserInAuth0.dtos.ParamsAuthApiV2UpdateUsers;
import com.oidccall.createUserInAuth0.dtos.ParamsAuthApiV2UsersDto;
import com.oidccall.createUserInAuth0.dtos.ParamsAuthApiV2VerifEmail;
import com.oidccall.createUserInAuth0.dtos.ResponseAuthApiV2UsersDto;
import com.oidccall.createUserInAuth0.dtos.ResponseAuthApiV2VerifEmail;
import com.oidccall.createUserInAuth0.interfaces.GetTokenWithFeign;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiV2UsersRequest {

  private final GetTokenWithFeign getTokenWithFeign;
  private final TokenFromAuth0 tokenFromAuth0;

  public ResponseAuthApiV2UsersDto createUserInAuth0(ParamsAuthApiV2UsersDto paramsAuthApiV2UsersDto) {
    return getTokenWithFeign.createUserApiV2Users(
        "Bearer " + this.tokenFromAuth0.getFullToken().getAccess_token(), paramsAuthApiV2UsersDto);
  }

  public ResponseAuthApiV2UsersDto getUserApiV2Users(String userId) {
    return getTokenWithFeign.getUserApiV2Users(
        "Bearer " + this.tokenFromAuth0.getFullToken().getAccess_token(), userId);
  }

  public void deleteUserApiV2Users(String userId) {
    getTokenWithFeign.deleteUserApiV2Users(
        "Bearer " + this.tokenFromAuth0.getFullToken().getAccess_token(), userId);
  }

  public ResponseAuthApiV2VerifEmail verificationEmail(ParamsAuthApiV2VerifEmail p1) {
    return getTokenWithFeign.verificationEmail("Bearer " + this.tokenFromAuth0.getFullToken().getAccess_token(), p1);
  }

  public ResponseAuthApiV2UsersDto updateUsers(String userId, ParamsAuthApiV2UpdateUsers p1) {
    return getTokenWithFeign.updateUsers("Bearer " + this.tokenFromAuth0.getFullToken().getAccess_token(), userId, p1);
  }

}
