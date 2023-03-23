package com.elcptn.mgmtsvc.repositories;

import com.elcptn.mgmtsvc.dto.StatusMetricDto;
import com.elcptn.mgmtsvc.entities.Event;
import com.elcptn.mgmtsvc.entities.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public interface EventRepository extends JpaRepository<Event, UUID> {


    @Query(value = "SELECT * FROM event WHERE state='QUEUED'" +
            " ORDER BY created_at FOR UPDATE SKIP LOCKED", nativeQuery = true)
    Stream<Event> fetchEventsForProcessing();

    @Modifying
    @Transactional
    @Query(value = "UPDATE Event SET state=:state where id = :eventId")
    void updateEventState(UUID eventId, State state);

    @Query(value = "SELECT new com.elcptn.mgmtsvc.dto.StatusMetricDto(e.state, COUNT(e.id)) " +
            "FROM Event e WHERE e.createdAt >= :createdAfter GROUP BY e.state")
    List<StatusMetricDto> getStatusCountsForCollectionRun(@Param("createdAfter") ZonedDateTime createdAfter);

    @Query(value = "SELECT new com.elcptn.mgmtsvc.dto.StatusMetricDto(e.state, COUNT(e.id)) " +
            "FROM Event e WHERE e.source.id = :source AND e.createdAt >= :createdAfter GROUP BY e.state")
    List<StatusMetricDto> getStatusCountsForCollectionRun(@Param("source") UUID sourceId, @Param(
            "createdAfter") ZonedDateTime createdAfter);
}