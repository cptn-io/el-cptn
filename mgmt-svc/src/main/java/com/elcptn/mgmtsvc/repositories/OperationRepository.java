package com.elcptn.mgmtsvc.repositories;

import com.elcptn.mgmtsvc.entities.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OperationRepository extends JpaRepository<Operation, UUID> {

    Page<Operation> findAllByApp(UUID appId, Pageable pageable);
}