package io.cptn.common.repositories;

import io.cptn.common.entities.ExtractorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface ExtractorScheduleRepository extends JpaRepository<ExtractorSchedule, UUID> {

    List<ExtractorSchedule> findByExtractorId(UUID extractorId);

    Long countByExtractorId(UUID extractorId);

    Stream<ExtractorSchedule> findExtractorScheduleByActiveTrueAndNextRunAtLessThanEqual(ZonedDateTime now);
}