package io.cptn.common.repositories;

import io.cptn.common.entities.Transformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransformationRepository extends JpaRepository<Transformation, UUID> {
}