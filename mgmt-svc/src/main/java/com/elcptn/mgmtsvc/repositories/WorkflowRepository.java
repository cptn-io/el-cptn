package com.elcptn.mgmtsvc.repositories;

import com.elcptn.mgmtsvc.entities.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/* @author: kc, created on 2/7/23 */

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, UUID> {

}
