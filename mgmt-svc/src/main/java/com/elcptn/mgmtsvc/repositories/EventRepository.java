package com.elcptn.mgmtsvc.repositories;

import com.elcptn.mgmtsvc.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
}