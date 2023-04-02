package com.elcptn.mgmtsvc.repositories;

import com.elcptn.common.entities.Destination;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DestinationRepository extends JpaRepository<Destination, UUID> {
}