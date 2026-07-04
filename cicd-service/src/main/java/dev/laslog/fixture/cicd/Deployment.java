package dev.laslog.fixture.cicd;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
class Deployment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String environment;

    private String revision;
    private Instant deployedAt;

    protected Deployment() {
    }

    Deployment(String environment, String revision, Instant deployedAt) {
        this.environment = environment;
        this.revision = revision;
        this.deployedAt = deployedAt;
    }

    long id() {
        return id;
    }

    String environment() {
        return environment;
    }

    String revision() {
        return revision;
    }

    Instant deployedAt() {
        return deployedAt;
    }
}
