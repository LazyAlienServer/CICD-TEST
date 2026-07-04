package dev.laslog.fixture.gateway;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(GatewayConfiguration.CicdClientProperties.class)
class GatewayConfiguration {

    @Bean
    RestClient cicdRestClient(CicdClientProperties properties, RestClient.Builder builder) {
        return builder.baseUrl(properties.baseUrl()).build();
    }

    @ConfigurationProperties(prefix = "fixture.cicd-service")
    record CicdClientProperties(String baseUrl) {
    }
}
