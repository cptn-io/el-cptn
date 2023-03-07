package com.elcptn.mgmtsvc.repositories;

import com.elcptn.mgmtsvc.entities.Pipeline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PipelineRepository extends JpaRepository<Pipeline, UUID> {
}