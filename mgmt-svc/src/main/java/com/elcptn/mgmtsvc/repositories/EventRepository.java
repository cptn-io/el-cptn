package com.elcptn.mgmtsvc.repositories;

import com.elcptn.mgmtsvc.entities.Event;
import com.elcptn.mgmtsvc.entities.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE event SET state='IN_PROGRESS' WHERE id IN (SELECT id FROM event e WHERE e" +
            ".state='QUEUED' " +
            "ORDER BY " +
            "created_at " +
            "FOR UPDATE " +
            "SKIP " +
            "LOCKED LIMIT :size) RETURNING *", nativeQuery = true)
    List<Event> fetchEventsForProcessing(int size);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Event SET state=:state where id = :eventId")
    void updateEventState(UUID eventId, State state);
}