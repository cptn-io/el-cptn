package com.elcptn.mgmtsvc.repositories;

import com.elcptn.mgmtsvc.entities.Action;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActionRepository extends JpaRepository<Action, UUID> {
}