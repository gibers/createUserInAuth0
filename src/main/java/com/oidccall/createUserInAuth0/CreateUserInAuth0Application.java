package com.oidccall.createUserInAuth0;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static java.util.Arrays.stream;

@Log4j2
// todo: remove this scanBasePackages
@SpringBootApplication(scanBasePackages = {"com.oidccall.createUserInAuth0"})
@ConfigurationPropertiesScan
@EnableJpaAuditing
public class CreateUserInAuth0Application {

	enum DotEnv {
		MA_VARIABLE_PROFILE,
		LOG_LEVEL_ROOT
	}

	public static void main(String[] args) {
		dotEnvSafeCheck();
		SpringApplication.run(CreateUserInAuth0Application.class, args);
	}

	@Bean
	public AuditorAware<String> auditorProvider() {
		return () -> {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication == null || !authentication.isAuthenticated()) {
				return Optional.of("system");
			}
			return Optional.of(authentication.getName());
		};
	}

	private static void dotEnvSafeCheck() {
		final var dotenv = Dotenv.configure()
			.ignoreIfMissing()
			.load();

		stream(DotEnv.values())
			.map(DotEnv::name)
			.filter(varName -> dotenv.get(varName, "").isEmpty())
			.findFirst()
			.ifPresent(varName -> {
				log.error("[Fatal] Missing or empty environment variable: {}", varName);

				System.exit(1);
			});
	}

}
