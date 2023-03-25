package com.elcptn.mgmtsvc.repositories;

import com.elcptn.mgmtsvc.dto.StatusMetricDto;
import com.elcptn.mgmtsvc.entities.OutboundEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface OutboundEventRepository extends JpaRepository<OutboundEvent, UUID> {

    @Query(value = "SELECT new com.elcptn.mgmtsvc.dto.StatusMetricDto(e.state, COUNT(e.id)) " +
            "FROM OutboundEvent e WHERE e.createdAt >= :createdAfter GROUP BY e.state")
    List<StatusMetricDto> getStatusCountsForOutboundEvents(@Param("createdAfter") ZonedDateTime createdAfter);
}