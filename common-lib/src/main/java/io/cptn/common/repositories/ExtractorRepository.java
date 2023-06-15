package io.cptn.common.repositories;

import io.cptn.common.entities.Extractor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExtractorRepository extends JpaRepository<Extractor, UUID> {

    Optional<Extractor> findBySourceId(UUID sourceId);
}