package dev.laslog.fixture.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.MockServerRestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(properties = "fixture.cicd-service.base-url=http://cicd-service.test")
class GatewayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MockServerRestClientCustomizer restClientCustomizer;

    @Test
    void testEndpointReportsGatewayStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/test"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.service", is("gateway-service")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", is("ok")));
    }

    @Test
    void pipelineSummaryAggregatesBackendStatus() throws Exception {
        MockRestServiceServer server = restClientCustomizer.getServer();
        server.expect(requestTo("http://cicd-service.test/api/test"))
                .andRespond(withSuccess("{\"service\":\"cicd-service\",\"status\":\"ok\",\"message\":\"ready\",\"version\":\"test\"}", MediaType.APPLICATION_JSON));
        server.expect(requestTo("http://cicd-service.test/api/deployments"))
                .andRespond(withSuccess("[{\"id\":1,\"environment\":\"ci\",\"revision\":\"abc\",\"deployedAt\":\"1970-01-01T00:00:00Z\"}]", MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/pipeline-summary"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.backendService", is("cicd-service")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.backendStatus", is("ok")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deploymentCount", is(1)));

        server.verify();
    }

    @Test
    void readinessEndpointIsAvailableForSmokeChecks() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health/readiness"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", is("UP")));
    }

    @TestConfiguration
    static class RestClientTestConfiguration {

        @Bean
        MockServerRestClientCustomizer mockServerRestClientCustomizer() {
            return new MockServerRestClientCustomizer();
        }

        @Bean
        RestClient.Builder restClientBuilder(MockServerRestClientCustomizer customizer) {
            RestClient.Builder builder = RestClient.builder();
            customizer.customize(builder);
            return builder;
        }
    }
}
