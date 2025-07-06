package com.oidccall.createUserInAuth0;

import com.oidccall.createUserInAuth0.entities.Users;
import com.oidccall.createUserInAuth0.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.Optional;

@SpringBootTest
@Slf4j
class CreateUserInAuth0ApplicationTests {

	@Autowired
  private UsersRepository usersRepository;

	@Test
	void testDateAfter() {
		Optional<Users> userInDB = this.usersRepository.findByAuth0UserId("auth0|456");
		Assertions.assertTrue(userInDB.isPresent());

		long _10minutes = 1000 * 60 * 10;
		long rajout = new Date().getTime() + _10minutes;
		Date date1 = new Date(rajout);
		log.debug("date1: {}", date1);
		// THEN:
		Assertions.assertTrue(date1.after(new Date()));
	}

}