package io.cptn.common.repositories;

import io.cptn.common.entities.PipelineTrigger;
import io.cptn.common.entities.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/* @author: kc, created on 4/3/23 */
@Repository
public interface PipelineTriggerRepository extends JpaRepository<PipelineTrigger, UUID> {

    Long countByPipelineIdAndStateEquals(UUID pipelineId, State state);
}
