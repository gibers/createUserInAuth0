package com.oidccall.createUserInAuth0.implementation;

import com.oidccall.createUserInAuth0.dtos.ResponseAuthApiV2UsersDto;
import com.oidccall.createUserInAuth0.dtos.mappers.UsersEntityMapper;
import com.oidccall.createUserInAuth0.entities.Users;
import com.oidccall.createUserInAuth0.feignCalls.ApiV2UsersRequest;
import com.oidccall.createUserInAuth0.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.util.ArrayList;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
@Slf4j
public class UpsertUserFromAuth0ToLocalDBProcess {

  private final ApiV2UsersRequest apiV2UsersRequest;
  private final UsersRepository usersRepository;

  private ResponseAuthApiV2UsersDto responseAuthApiV2UsersDto;
  private Users usersFromDB;

  public void process(String userId) {
    // 1. get the user from auth0
    this.responseAuthApiV2UsersDto = this.retrieveUserFromAuth0(userId);
    // 2. existe the user in local DB?
    if (!this.existUserInLocalDB(userId)) {
      // 3. if not exist, create it
      this.insertUserInLocalDB();
      return;
    }
    // 4. unconditionaly update the user in local DB
    this.usersFromDB = this.updateEmailVerified();
    // 5. looking for differences between the user in DB and the user in request
    ArrayList<UtilsUserFonctions.ChangeType> changeType = isEqualsBetweenUserInDBAndUserInAuth0();
    this.updateUsersInDB(changeType);
  }

  private void updateUsersInDB(ArrayList<UtilsUserFonctions.ChangeType> changeType) {
    if (changeType.isEmpty()) return;
    this.usersFromDB.setEmail(this.responseAuthApiV2UsersDto.getEmail());
    this.usersFromDB.setFamily_name(this.responseAuthApiV2UsersDto.getFamily_name());
    this.usersFromDB.setGiven_name(this.responseAuthApiV2UsersDto.getGiven_name());
    this.usersFromDB.setNickname(this.responseAuthApiV2UsersDto.getNickname());
    this.usersFromDB.setPhone_number(this.responseAuthApiV2UsersDto.getPhone_number());
    this.usersFromDB.setGender(UtilsUserFonctions.transformUserMetada.apply(this.responseAuthApiV2UsersDto.getUser_metadata()).getGender());
    this.usersRepository.save(this.usersFromDB);
  }

  private ArrayList<UtilsUserFonctions.ChangeType> isEqualsBetweenUserInDBAndUserInAuth0() {
    var listChangeType = new ArrayList<UtilsUserFonctions.ChangeType>();
    boolean userInDBEqualsUserInRequest = this.usersFromDB.getEmail().equalsIgnoreCase(this.responseAuthApiV2UsersDto.getEmail());
    if (!userInDBEqualsUserInRequest) {
      listChangeType.add(UtilsUserFonctions.ChangeType.CHANGE_EMAIL);
    };
    userInDBEqualsUserInRequest = UtilsUserFonctions.isEqualsIgnoreCaseAndNull.apply(this.usersFromDB.getPhone_number(), this.responseAuthApiV2UsersDto.getPhone_number());
    if (!userInDBEqualsUserInRequest) {
      listChangeType.add(UtilsUserFonctions.ChangeType.CHANGE_PHONENUMBER);
    }
    userInDBEqualsUserInRequest = UtilsUserFonctions.isEqualsIgnoreCaseAndNull.apply(this.usersFromDB.getNickname(), this.responseAuthApiV2UsersDto.getNickname());
    userInDBEqualsUserInRequest = userInDBEqualsUserInRequest &&
      UtilsUserFonctions.isEqualsIgnoreCaseAndNull.apply(this.usersFromDB.getFamily_name(), this.responseAuthApiV2UsersDto.getFamily_name());
    userInDBEqualsUserInRequest = userInDBEqualsUserInRequest &&
      UtilsUserFonctions.isEqualsIgnoreCaseAndNull.apply(this.usersFromDB.getGiven_name(), this.responseAuthApiV2UsersDto.getGiven_name());
    if (!userInDBEqualsUserInRequest) {
      listChangeType.add(UtilsUserFonctions.ChangeType.CHANGE_DATA);
    }
    var userMetadaTemp = UtilsUserFonctions.transformUserMetada.apply(this.responseAuthApiV2UsersDto.getUser_metadata());
    if (this.usersFromDB.getGender() != userMetadaTemp.getGender()) {
      listChangeType.add(UtilsUserFonctions.ChangeType.CHANGE_GENDER);
    }
    return listChangeType;
  }

  private ResponseAuthApiV2UsersDto retrieveUserFromAuth0(String userId) {
    return this.apiV2UsersRequest.getUserApiV2Users(userId);
  }

  private boolean existUserInLocalDB(String userId) {
    // todo: make a test that verifies that deleted users are not returned by this method
    return this.usersRepository.existsByAuth0UserIdAndDeletedIsFalse(userId);
  }

  private void insertUserInLocalDB() {
    Users users = UsersEntityMapper.mapToUsersEntity(this.responseAuthApiV2UsersDto);
    // todo: verify that users contains the correct gender, because it comes from the userMetadata.
    this.usersRepository.save(users);
  }

  private Users updateEmailVerified() {
    var usersFromDB = this.usersRepository.findByAuth0UserIdAndDeletedIsFalse(this.responseAuthApiV2UsersDto.getUserId())
      .orElseThrow();
    if (usersFromDB.isEmail_verified() == (this.responseAuthApiV2UsersDto.isEmailVerified())) {
      return usersFromDB;
    }
    usersFromDB.setEmail_verified(this.responseAuthApiV2UsersDto.isEmailVerified());
    usersFromDB.setLast_modified_email_verified(Instant.now());
    return this.usersRepository.save(usersFromDB);
  }

}
