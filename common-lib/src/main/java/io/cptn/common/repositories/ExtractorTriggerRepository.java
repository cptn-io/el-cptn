package io.cptn.common.repositories;

import io.cptn.common.entities.ExtractorTrigger;
import io.cptn.common.entities.State;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExtractorTriggerRepository extends JpaRepository<ExtractorTrigger, UUID> {

    Long countByExtractorIdAndStateEquals(UUID extractorId, State state);

    @Modifying
    @Transactional
    @Query(value = "delete from extractor_trigger where created_at < now() - interval '7 days'",
            nativeQuery = true)
    void deleteStaleData();
}