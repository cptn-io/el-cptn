package com.elcptn.common.repositories;

import com.elcptn.common.entities.Destination;
import com.elcptn.common.projections.DestinationView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, UUID> {

    List<DestinationView> findAllProjectedBy(Pageable pageable);
}