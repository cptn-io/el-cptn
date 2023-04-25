package com.elcptn.common.repositories;

import com.elcptn.common.entities.Instance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InstanceRepository extends JpaRepository<Instance, UUID> {

    @Query(value = "FROM Instance i ORDER BY i.createdAt DESC LIMIT 1")
    Instance getInstance();
}