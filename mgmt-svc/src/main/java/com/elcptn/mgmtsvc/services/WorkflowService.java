package com.elcptn.mgmtsvc.services;

import com.elcptn.mgmtsvc.entities.Workflow;
import com.elcptn.mgmtsvc.helpers.ListEntitiesParam;
import com.elcptn.mgmtsvc.repositories.WorkflowRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WorkflowService extends CommonService {

    private final WorkflowRepository workflowRepository;

    public WorkflowService(WorkflowRepository repository) {
        this.workflowRepository = repository;
    }

    public void create(Workflow workflow) {

        if (workflow.getSecured()) {
            workflow.setupNewKeys();
        }
        workflowRepository.save(workflow);
    }

    public Optional<Workflow> getById(String id) {
        return workflowRepository.findById(UUID.fromString(id));
    }

    public void update(Workflow workflow) {
        if (workflow.getSecured() && !workflow.hasAnyKeysSetup()) {
            workflow.setupNewKeys();
        }
        workflowRepository.save(workflow);
    }

    public void rotateKeys(Workflow workflow) {
        workflow.rotateKeys();
        workflowRepository.save(workflow);
    }

    public List<Workflow> getAll(ListEntitiesParam param) {
        Pageable pageable = getPageable(param);
        return workflowRepository.findAll(pageable).stream().collect(Collectors.toList());
    }

    public long count() {
        return workflowRepository.count();
    }

    public void delete(Workflow workflow) {
        workflowRepository.delete(workflow);
    }
}
