package com.elcptn.common.repositories;

import com.elcptn.common.entities.OutboundWriteEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OutboundWriteEventRepository extends JpaRepository<OutboundWriteEvent, UUID> {
}