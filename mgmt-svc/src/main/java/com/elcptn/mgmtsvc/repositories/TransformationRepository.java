package com.elcptn.mgmtsvc.repositories;

import com.elcptn.mgmtsvc.entities.Transformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransformationRepository extends JpaRepository<Transformation, UUID> {
}