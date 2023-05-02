package io.cptn.common.repositories;

import io.cptn.common.entities.Destination;
import io.cptn.common.projections.DestinationView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, UUID> {

    List<DestinationView> findAllProjectedBy(Pageable pageable);
}