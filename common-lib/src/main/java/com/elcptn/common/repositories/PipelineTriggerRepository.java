package com.elcptn.common.repositories;

import com.elcptn.common.entities.PipelineTrigger;
import com.elcptn.common.entities.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/* @author: kc, created on 4/3/23 */
@Repository
public interface PipelineTriggerRepository extends JpaRepository<PipelineTrigger, UUID> {

    Long countByPipelineIdAndStateEquals(UUID pipelineId, State state);
}
