package com.elcptn.common.repositories;

import com.elcptn.common.entities.InboundWriteEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InboundWriteEventRepository extends JpaRepository<InboundWriteEvent, UUID> {


}