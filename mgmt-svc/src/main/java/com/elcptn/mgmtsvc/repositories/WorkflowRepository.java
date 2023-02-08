package com.elcptn.mgmtsvc.repositories;

import com.elcptn.mgmtsvc.entities.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, String>  {

}
