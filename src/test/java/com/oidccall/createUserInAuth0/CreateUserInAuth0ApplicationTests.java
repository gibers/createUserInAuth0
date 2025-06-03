package com.oidccall.createUserInAuth0;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

@SpringBootTest
@Slf4j
@ActiveProfiles("docker")
class CreateUserInAuth0ApplicationTests {

	@Test
	void testDateAfter() {
		long _10minutes = 1000 * 60 * 10;
		long rajout = new Date().getTime() + _10minutes;
		Date date1 = new Date(rajout);
		log.debug("date1: {}", date1);
		// THEN:
		Assertions.assertTrue(date1.after(new Date()));
	}

}
