package com.oidccall.createUserInAuth0.implementation;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oidccall.createUserInAuth0.dtos.front.FrontUserToCreateDto;
import com.oidccall.createUserInAuth0.dtos.front.SimpleUserData;
import com.oidccall.createUserInAuth0.dtos.mappers.UsersEntityMapper;
import com.oidccall.createUserInAuth0.entities.BatchToUser;
import com.oidccall.createUserInAuth0.entities.Users;
import com.oidccall.createUserInAuth0.entities.UsersDeleted;
import com.oidccall.createUserInAuth0.exceptions.ErrorsEnum;
import com.oidccall.createUserInAuth0.repository.BatchToUserRepository;
import com.oidccall.createUserInAuth0.repository.UsersDeletedRepository;
import com.oidccall.createUserInAuth0.repository.UsersRepository;
import com.oidccall.dtos.enums.EmailStatusEnum;
import com.oidccall.dtos.feign.ParamsAuthApiV2UpdateVerifiedEmail;
import com.oidccall.dtos.feign.ParamsAuthApiV2UsersDto;
import com.oidccall.dtos.feign.ResponseAuthApiV2UsersDto;
import com.oidccall.feigncallslib.feignCalls.ApiV2UsersRequestLib;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserImplementation {

	private final ApiV2UsersRequestLib apiV2UsersRequest;
	private final UsersRepository usersRepository;
	private final UsersDeletedRepository usersDeletedRepository;
	private final UpsertUserFromAuth0ToLocalDBProcess upsertUserFromAuth0ToLocalDBProcess;
	private final UpdateUserInAuth0Process updateUserInAuth0Process;
	private final BatchToUserRepository batchToUserRepository;

	public void deleteUserInAuth0(String userId, long jobExecution, String stepName) {
		this.apiV2UsersRequest.deleteUserApiV2Users(userId);
		this.passUserToUserDeletedOrThrow(userId, jobExecution, stepName);
	}

	public ResponseAuthApiV2UsersDto createUserInAuth0(FrontUserToCreateDto userFromFront) throws JsonProcessingException {
		var paramsAuthApiV2UsersDto = userFromFront.toParamsForCreation();
		paramsAuthApiV2UsersDto = generateNicknameForCreation(paramsAuthApiV2UsersDto);
		ResponseAuthApiV2UsersDto userFromAuth0 = this.apiV2UsersRequest.createUserInAuth0(paramsAuthApiV2UsersDto);
		Users users = UsersEntityMapper.mapToUsersEntity(userFromAuth0, userFromFront);
		usersRepository.save(users);
		log.debug("userFromAuth0: {}", users);
		return userFromAuth0;
	}

	public void upsertUserInLocalDBImplementation(String userId) {
		this.upsertUserFromAuth0ToLocalDBProcess.process(userId);
	}

	public void updateUserInAuth0Implementation(String userId, @Valid SimpleUserData simpleUserData) {
		this.updateUserInAuth0Process.process(userId, simpleUserData);
	}

	public Users passEmailToUnVerified(String userId) {
		// 1. update auth0.
		this.apiV2UsersRequest.updateUsers(userId, new ParamsAuthApiV2UpdateVerifiedEmail(false));
		// 2. update local db.
		this.usersRepository.findByAuth0UserIdAndDeletedIsFalse(userId).ifPresentOrElse(x -> {
			x.setEmail_verified(false);
			x.setEmailStatus(EmailStatusEnum.PASSED_TO_UNVERIFIED);
			x.setLastModifiedEmailVerified(Instant.now());
			this.usersRepository.save(x);
		}, () -> {
			String format = String.format(ErrorsEnum.E_1003.getOriginaErrorMessage(), userId);
			log.error(format);
			throw new EntityNotFoundException(format);
		});
		return this.usersRepository.findByAuth0UserIdAndDeletedIsFalse(userId).orElseThrow();
	}

	public List<String> updateEmailThenReturnUserWhichEmailIsNotVerified(EmailStatusEnum emailStatusEnum, LocalDate dateLimit) {
		this.getUserIdWhichHaveEmailStatusOverDateLimit(emailStatusEnum, dateLimit).forEach(this.upsertUserFromAuth0ToLocalDBProcess::process);
		return this.getUserIdWhichHaveEmailStatusOverDateLimit(emailStatusEnum, dateLimit);
	}

	public void insertNewEntryInBatchToUser(Users users, long jobExecutionId, String stepName) {
		BatchToUser batchToUser = new BatchToUser();
		batchToUser.setUser(users);
		batchToUser.setEmailStatus(EmailStatusEnum.PASSED_TO_UNVERIFIED);
		batchToUser.setJobExecutionId(jobExecutionId);
		batchToUser.setStepName(stepName);
		this.batchToUserRepository.save(batchToUser);
	}

	// -------------------------------------------------------

	private List<String> getUserIdWhichHaveEmailStatusOverDateLimit(EmailStatusEnum emailStatusEnum, LocalDate dateLimit) {
		Instant startOfDayUtc = dateLimit.atStartOfDay(ZoneOffset.UTC).toInstant();
		return this.usersRepository.findAllByEmailStatusAndLastModifiedEmailVerifiedBefore(emailStatusEnum, startOfDayUtc)
				.stream().map(Users::getAuth0UserId)
				.toList();
	}

	private void passUserToUserDeletedOrThrow(String userId, long jobExecutionId, String stepName) {
		Optional<Users> byAuth0UserId = this.usersRepository.findByAuth0UserId(userId);
		byAuth0UserId.ifPresentOrElse(users -> {
			var modelMapper = new ModelMapper();
			UsersDeleted map = modelMapper.map(users, UsersDeleted.class);
			map.setJobExecutionId(jobExecutionId);
			map.setStepName(stepName);
			usersDeletedRepository.save(map);
			usersRepository.delete(users);
		}, () -> {
			String format = String.format(ErrorsEnum.E_1002.getOriginaErrorMessage(), userId);
			log.error(format);
			throw new EntityNotFoundException(format);
		});
	}

	private ParamsAuthApiV2UsersDto generateNicknameForCreation(ParamsAuthApiV2UsersDto paramsAuthApiV2UsersDto) {
		if (StringUtils.isNotBlank(paramsAuthApiV2UsersDto.nickname())) {
			return paramsAuthApiV2UsersDto;
		}
		return paramsAuthApiV2UsersDto.withNickname(composeNickname.apply(paramsAuthApiV2UsersDto));
	}

	private final BiFunction<String, Integer, String> truncatIt = (field, minSize) -> {
		int firstNameTruncated = Math.min(field.length(), minSize);
		return field.substring(0, 1).toUpperCase() + field.toLowerCase().substring(1, firstNameTruncated);
	};

	private final Function<ParamsAuthApiV2UsersDto, String> composeNickname =
			(firstName) -> truncatIt.apply(firstName.given_name(), 12) + "_" + truncatIt.apply(firstName.family_name(), 4) + "$";

}
