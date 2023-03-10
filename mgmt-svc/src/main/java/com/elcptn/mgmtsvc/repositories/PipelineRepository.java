package com.elcptn.mgmtsvc.repositories;

import com.elcptn.mgmtsvc.entities.Pipeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;
import java.util.UUID;

public interface PipelineRepository extends JpaRepository<Pipeline, UUID> {

    @Query(value = "SELECT p.id FROM Pipeline p WHERE p.active=true AND p.source.id=:sourceId")
    Set<UUID> findActivePipelinesBySource(UUID sourceId);
}