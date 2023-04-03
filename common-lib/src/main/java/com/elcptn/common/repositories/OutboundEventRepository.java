package com.elcptn.common.repositories;

import com.elcptn.common.entities.OutboundEvent;
import com.elcptn.common.pojos.StatusMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OutboundEventRepository extends JpaRepository<OutboundEvent, UUID> {

    @Query(value = "SELECT new com.elcptn.common.pojos.StatusMetric(e.state, COUNT(e.id)) " +
            "FROM OutboundEvent e WHERE e.createdAt >= :createdAfter GROUP BY e.state")
    List<StatusMetric> getStatusCountsForOutboundEvents(@Param("createdAfter") ZonedDateTime createdAfter);

    @Query(value = "SELECT new com.elcptn.common.pojos.StatusMetric(e.state, COUNT(e.id)) " +
            "FROM OutboundEvent e WHERE e.pipeline.id = :pipeline AND e.createdAt >= :createdAfter GROUP BY e.state")
    List<StatusMetric> getStatusCountsForOutboundEvents(@Param("pipeline") UUID pipelineId, @Param(
            "createdAfter") ZonedDateTime createdAfter);
}