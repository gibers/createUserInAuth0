package com.oidccall.createUserInAuth0;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.oidccall.createUserInAuth0.entities.Users;
import com.oidccall.createUserInAuth0.repository.UsersRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class CreateUserInAuth0ApplicationTest {

	@Autowired
	private UsersRepository usersRepository;

	@Test
	void testDateAfter() {
		String testAuth0UserId = "auth0|test-" + Long.toHexString(System.nanoTime());
		Users user = new Users();
		user.setAuth0UserId(testAuth0UserId);
		user.setEmail("test-date-after@example.com");

		this.usersRepository.saveAndFlush(user);

		try {
			Optional<Users> userInDB = this.usersRepository.findByAuth0UserId(testAuth0UserId);
			Assertions.assertTrue(userInDB.isPresent());

			long _10minutes = 1000 * 60 * 10;
			long rajout = new Date().getTime() + _10minutes;
			Date date1 = new Date(rajout);
			log.debug("date1: {}", date1);

			// THEN:
			Assertions.assertTrue(date1.after(new Date()));
		} finally {
			this.usersRepository.findByAuth0UserId(testAuth0UserId)
				.ifPresent(this.usersRepository::delete);
			this.usersRepository.flush();
		}
	}

}
