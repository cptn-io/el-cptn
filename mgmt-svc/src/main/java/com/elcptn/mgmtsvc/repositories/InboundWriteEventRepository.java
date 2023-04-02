package com.elcptn.mgmtsvc.repositories;

import com.elcptn.common.entities.InboundWriteEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InboundWriteEventRepository extends JpaRepository<InboundWriteEvent, UUID> {


}