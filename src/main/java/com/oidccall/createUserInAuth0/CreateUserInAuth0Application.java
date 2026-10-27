package com.oidccall.createUserInAuth0;

import static java.util.Arrays.stream;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableJpaAuditing
public class CreateUserInAuth0Application {

	enum DotEnv {
		MA_VARIABLE_PROFILE,
		LOG_LEVEL_ROOT
	}

	static void main(String[] args) {
		dotEnvSafeCheck();
		SpringApplication.run(CreateUserInAuth0Application.class, args);
	}

	@Bean
	public Map<String, String> loadTableColorCodes(ObjectMapper objectMapper) {
		ClassPathResource resource = new ClassPathResource("static/table-color-code.json");
		try (var inputStream = resource.getInputStream()) {
			return objectMapper.readValue(inputStream, new TypeReference<>() {});
		} catch (IOException exception) {
			throw new IllegalStateException("Unable to read static/table-color-code.json", exception);
		}
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
