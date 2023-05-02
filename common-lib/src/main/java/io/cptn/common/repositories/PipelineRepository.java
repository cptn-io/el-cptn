package io.cptn.common.repositories;

import io.cptn.common.entities.Pipeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PipelineRepository extends JpaRepository<Pipeline, UUID>, QuerydslPredicateExecutor<Pipeline> {

    @Query(value = "FROM Pipeline p WHERE p.active=true AND p.source.id=:sourceId")
    List<Pipeline> findBySource(UUID sourceId);

    @Query(value = "SELECT COUNT(p) FROM Pipeline p WHERE p.active=true AND p.source.id=:sourceId")
    long countBySource(UUID sourceId);
}