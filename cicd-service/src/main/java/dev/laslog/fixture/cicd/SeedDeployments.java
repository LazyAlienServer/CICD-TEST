package dev.laslog.fixture.cicd;

import java.time.Instant;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SeedDeployments {

    @Bean
    CommandLineRunner seedDeploymentData(DeploymentRepository deployments) {
        return args -> deployments.save(new Deployment("ci", "bootstrap", Instant.EPOCH));
    }
}
