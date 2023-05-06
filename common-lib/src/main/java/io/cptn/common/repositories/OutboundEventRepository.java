package io.cptn.common.repositories;

import io.cptn.common.entities.OutboundEvent;
import io.cptn.common.pojos.StatusMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OutboundEventRepository extends JpaRepository<OutboundEvent, UUID>, QuerydslPredicateExecutor<OutboundEvent> {

    @Query(value = "SELECT new io.cptn.common.pojos.StatusMetric(e.state, COUNT(e.id)) " +
            "FROM OutboundEvent e WHERE e.createdAt >= :createdAfter GROUP BY e.state")
    List<StatusMetric> getStatusCountsForOutboundEvents(@Param("createdAfter") ZonedDateTime createdAfter);

    @Query(value = "SELECT new io.cptn.common.pojos.StatusMetric(e.state, COUNT(e.id)) " +
            "FROM OutboundEvent e WHERE e.pipeline.id = :pipeline AND e.createdAt >= :createdAfter GROUP BY e.state")
    List<StatusMetric> getStatusCountsForOutboundEvents(@Param("pipeline") UUID pipelineId, @Param(
            "createdAfter") ZonedDateTime createdAfter);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM outbound_queue WHERE outbound_queue.created_at < :createdBefore", nativeQuery = true)
    void purgeStaleDataInOutboundQueue(@Param("createdBefore") ZonedDateTime createdBefore);
}