package com.oidccall.createUserInAuth0.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class MaVariablePropertiesTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration.class))
        .withPropertyValues("mavariable.profile=test-profile")
        .withUserConfiguration(MaVariableProperties.class);

    @Test
    void shouldBindProfileProperty() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(MaVariableProperties.class);
            MaVariableProperties properties = context.getBean(MaVariableProperties.class);
            assertThat(properties.getProfile()).isEqualTo("test-profile");
        });
    }
}
