package com.elcptn.common.repositories;

import com.elcptn.common.entities.PipelineSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PipelineScheduleRepository extends JpaRepository<PipelineSchedule, UUID> {

    List<PipelineSchedule> findByPipelineId(UUID pipelineId);

    Long countByPipelineId(UUID pipelineId);

    List<PipelineSchedule> findPipelineScheduleByActiveTrueAndNextRunAtLessThanEqual(ZonedDateTime now);
}