package com.elcptn.mgmtsvc.services;

import com.elcptn.mgmtsvc.entities.Workflow;
import com.elcptn.mgmtsvc.helpers.ListEntitiesParam;
import com.elcptn.mgmtsvc.repositories.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/* @author: kc, created on 2/7/23 */

@Service
@RequiredArgsConstructor
public class WorkflowService extends CommonService {

    private final WorkflowRepository workflowRepository;

    public Workflow create(Workflow workflow) {

        if (workflow.getSecured()) {
            workflow.setupNewKeys();
        }
        return workflowRepository.save(workflow);
    }

    public Optional<Workflow> getById(UUID id) {
        return workflowRepository.findById(id);
    }

    public Workflow update(Workflow workflow) {
        if (workflow.getSecured() && !workflow.hasAnyKeysSetup()) {
            workflow.setupNewKeys();
        }
        return workflowRepository.save(workflow);
    }

    public Workflow rotateKeys(Workflow workflow) {
        workflow.rotateKeys();
        return workflowRepository.save(workflow);
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
