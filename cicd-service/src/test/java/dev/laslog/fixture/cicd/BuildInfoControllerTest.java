package dev.laslog.fixture.cicd;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BuildInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testEndpointReportsServiceStatus() throws Exception {
        mockMvc.perform(get("/api/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.service", is("cicd-service")))
                .andExpect(jsonPath("$.status", is("ok")))
                .andExpect(jsonPath("$.message", is("cicd-service is running")));
    }

    @Test
    void healthEndpointIsAvailableForSmokeChecks() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")));
    }

    @Test
    void deploymentEndpointPersistsCreatedDeployment() throws Exception {
        mockMvc.perform(post("/api/deployments")
                        .contentType("application/json")
                        .content("{\"environment\":\"staging\",\"revision\":\"abc123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.environment", is("staging")))
                .andExpect(jsonPath("$.revision", is("abc123")));

        mockMvc.perform(get("/api/deployments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.environment == 'staging')].revision", is(List.of("abc123"))));
    }

    @Test
    void deploymentEndpointRejectsInvalidInput() throws Exception {
        mockMvc.perform(post("/api/deployments")
                        .contentType("application/json")
                        .content("{\"environment\":\"\",\"revision\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}
