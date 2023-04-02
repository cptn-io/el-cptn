package com.elcptn.common.repositories;

import com.elcptn.common.entities.InboundEvent;
import com.elcptn.common.entities.State;
import com.elcptn.common.pojos.StatusMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface InboundEventRepository extends JpaRepository<InboundEvent, UUID> {

    @Query(value = "SELECT * FROM inbound_queue WHERE state='QUEUED'" +
            " ORDER BY created_at FOR UPDATE SKIP LOCKED", nativeQuery = true)
    Stream<InboundEvent> fetchEventsForProcessing();

    @Query(value = "SELECT new com.elcptn.common.pojos.StatusMetric(e.state, COUNT(e.id)) " +
            "FROM InboundEvent e WHERE e.createdAt >= :createdAfter GROUP BY e.state")
    List<StatusMetric> getStatusCountsForEvents(@Param("createdAfter") ZonedDateTime createdAfter);

    @Query(value = "SELECT new com.elcptn.common.pojos.StatusMetric(e.state, COUNT(e.id)) " +
            "FROM InboundEvent e WHERE e.source.id = :source AND e.createdAt >= :createdAfter GROUP BY e.state")
    List<StatusMetric> getStatusCountsForEvents(@Param("source") UUID sourceId, @Param(
            "createdAfter") ZonedDateTime createdAfter);

    @Modifying
    @Transactional
    @Query(value = "UPDATE InboundEvent SET state=:state where id = :eventId")
    void updateEventState(UUID eventId, State state);
}