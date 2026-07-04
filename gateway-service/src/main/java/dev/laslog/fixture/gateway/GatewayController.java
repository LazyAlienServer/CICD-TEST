package dev.laslog.fixture.gateway;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api")
class GatewayController {

    private final String serviceName;
    private final String version;
    private final RestClient cicdRestClient;
    private final GatewayConfiguration.CicdClientProperties properties;

    GatewayController(
            @Value("${spring.application.name}") String serviceName,
            @Value("${fixture.version}") String version,
            RestClient cicdRestClient,
            GatewayConfiguration.CicdClientProperties properties) {
        this.serviceName = serviceName;
        this.version = version;
        this.cicdRestClient = cicdRestClient;
        this.properties = properties;
    }

    @GetMapping("/test")
    GatewayStatus test() {
        return new GatewayStatus(serviceName, "ok", version);
    }

    @GetMapping("/routes")
    List<RouteDescriptor> routes() {
        return List.of(
                new RouteDescriptor("cicd-status", properties.baseUrl() + "/api/test"),
                new RouteDescriptor("cicd-deployments", properties.baseUrl() + "/api/deployments"));
    }

    @GetMapping("/pipeline-summary")
    PipelineSummary pipelineSummary() {
        CicdStatus backend = cicdRestClient.get()
                .uri("/api/test")
                .retrieve()
                .body(CicdStatus.class);

        DeploymentSummary[] deployments = cicdRestClient.get()
                .uri("/api/deployments")
                .retrieve()
                .body(DeploymentSummary[].class);

        int deploymentCount = deployments == null ? 0 : deployments.length;
        return new PipelineSummary(serviceName, backend.service(), backend.status(), deploymentCount, version);
    }

    record GatewayStatus(String service, String status, String version) {
    }

    record RouteDescriptor(String id, String target) {
    }

    record CicdStatus(String service, String status, String message, String version) {
    }

    record DeploymentSummary(long id, String environment, String revision, String deployedAt) {
    }

    record PipelineSummary(String service, String backendService, String backendStatus, int deploymentCount, String version) {
    }
}
