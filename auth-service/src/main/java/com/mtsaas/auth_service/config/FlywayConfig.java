package com.mtsaas.auth_service.config;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class FlywayConfig {

    @Bean(initMethod = "migrate")
    @ConditionalOnProperty(prefix = "spring.flyway", name = "enabled", matchIfMissing = true)
    public Flyway flyway(DataSource dataSource, Environment environment) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations(environment.getProperty("spring.flyway.locations", "classpath:db/migration"))
                .baselineOnMigrate(environment.getProperty("spring.flyway.baseline-on-migrate", Boolean.class, true))
                .cleanOnValidationError(environment.getProperty("spring.flyway.clean-on-validation-error", Boolean.class, false))
                .load();
    }
}
