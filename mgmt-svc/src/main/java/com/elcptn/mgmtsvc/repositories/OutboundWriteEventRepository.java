package com.elcptn.mgmtsvc.repositories;

import com.elcptn.common.entities.OutboundWriteEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutboundWriteEventRepository extends JpaRepository<OutboundWriteEvent, UUID> {
}