package dev.laslog.fixture.cicd;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

interface DeploymentRepository extends JpaRepository<Deployment, Long> {

    List<Deployment> findAllByOrderByIdAsc();
}
