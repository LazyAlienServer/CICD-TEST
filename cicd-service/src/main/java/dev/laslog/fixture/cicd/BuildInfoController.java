package dev.laslog.fixture.cicd;

import java.time.Instant;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
class BuildInfoController {

    private final String serviceName;
    private final String version;
    private final DeploymentRepository deployments;

    BuildInfoController(
            @Value("${spring.application.name}") String serviceName,
            @Value("${fixture.version}") String version,
            DeploymentRepository deployments) {
        this.serviceName = serviceName;
        this.version = version;
        this.deployments = deployments;
    }

    @GetMapping("/test")
    ServiceStatus test() {
        return new ServiceStatus(serviceName, "ok", "cicd-service is running", version);
    }

    @GetMapping("/build-info")
    BuildInfo buildInfo() {
        return new BuildInfo(serviceName, version, Instant.EPOCH.toString());
    }

    @GetMapping("/deployments")
    List<DeploymentSummary> deployments() {
        return deployments.findAllByOrderByIdAsc().stream()
                .map(DeploymentSummary::from)
                .toList();
    }

    @PostMapping("/deployments")
    @ResponseStatus(HttpStatus.CREATED)
    DeploymentSummary createDeployment(@Valid @RequestBody DeploymentRequest request) {
        Deployment deployment = new Deployment(request.environment(), request.revision(), Instant.now());
        return DeploymentSummary.from(deployments.save(deployment));
    }

    record ServiceStatus(String service, String status, String message, String version) {
    }

    record BuildInfo(String service, String version, String fixtureTimestamp) {
    }

    record DeploymentRequest(@NotBlank String environment, @NotBlank String revision) {
    }

    record DeploymentSummary(long id, String environment, String revision, String deployedAt) {

        static DeploymentSummary from(Deployment deployment) {
            return new DeploymentSummary(
                    deployment.id(),
                    deployment.environment(),
                    deployment.revision(),
                    deployment.deployedAt().toString());
        }
    }
}
