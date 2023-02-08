package com.elcptn.mgmtsvc.services;

import com.elcptn.mgmtsvc.entities.Workflow;
import com.elcptn.mgmtsvc.repositories.WorkflowRepository;
import org.springframework.stereotype.Service;

@Service
public class WorkflowService {

    private final WorkflowRepository workflowRepository;

    public WorkflowService(WorkflowRepository repository) {
        this.workflowRepository = repository;
    }

    public Workflow create(Workflow workflow) {

        if (workflow.getSecured()) {
            workflow.setupNewKeys();
        }
        workflowRepository.save(workflow);
        return workflow;
    }
}
