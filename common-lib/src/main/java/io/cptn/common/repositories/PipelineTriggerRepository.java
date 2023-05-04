package io.cptn.common.repositories;

import io.cptn.common.entities.PipelineTrigger;
import io.cptn.common.entities.State;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/* @author: kc, created on 4/3/23 */
@Repository
public interface PipelineTriggerRepository extends JpaRepository<PipelineTrigger, UUID> {

    Long countByPipelineIdAndStateEquals(UUID pipelineId, State state);

    @Modifying
    @Transactional
    @Query(value = "delete from pipeline_trigger where created_at < now() - interval '7 days'",
            nativeQuery = true)
    void deleteStaleData();
}
