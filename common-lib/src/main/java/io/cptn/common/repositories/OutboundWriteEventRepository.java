package io.cptn.common.repositories;

import io.cptn.common.entities.OutboundWriteEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OutboundWriteEventRepository extends JpaRepository<OutboundWriteEvent, UUID> {
}