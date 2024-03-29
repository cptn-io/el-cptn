package io.cptn.common.repositories;

import io.cptn.common.entities.InboundEvent;
import io.cptn.common.entities.State;
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
import java.util.stream.Stream;

@Repository
public interface InboundEventRepository extends JpaRepository<InboundEvent, UUID>, QuerydslPredicateExecutor<InboundEvent> {

    @Query(value = "SELECT * FROM inbound_queue WHERE state='QUEUED'" +
            " ORDER BY created_at FOR UPDATE SKIP LOCKED", nativeQuery = true)
    Stream<InboundEvent> fetchEventsForProcessing();

    @Query(value = "SELECT new io.cptn.common.pojos.StatusMetric(e.state, COUNT(e.id)) " +
            "FROM InboundEvent e WHERE e.createdAt >= :createdAfter GROUP BY e.state")
    List<StatusMetric> getStatusCountsForEvents(@Param("createdAfter") ZonedDateTime createdAfter);

    @Query(value = "SELECT new io.cptn.common.pojos.StatusMetric(e.state, COUNT(e.id)) " +
            "FROM InboundEvent e WHERE e.source.id = :source AND e.createdAt >= :createdAfter GROUP BY e.state")
    List<StatusMetric> getStatusCountsForEvents(@Param("source") UUID sourceId, @Param(
            "createdAfter") ZonedDateTime createdAfter);

    @Modifying
    @Transactional
    @Query(value = "UPDATE InboundEvent SET state=:state where id = :eventId")
    void updateEventState(UUID eventId, State state);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM inbound_queue WHERE inbound_queue.created_at < :createdBefore", nativeQuery = true)
    void purgeStaleDataInInboundQueue(@Param("createdBefore") ZonedDateTime createdBefore);
}