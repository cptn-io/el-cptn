package com.elcptn.mgmtsvc.controllers;

import com.elcptn.mgmtsvc.dto.WorkflowDto;
import com.elcptn.mgmtsvc.entities.Workflow;
import com.elcptn.mgmtsvc.exceptions.BadRequestException;
import com.elcptn.mgmtsvc.mappers.WorkflowMapper;
import com.elcptn.mgmtsvc.services.WorkflowService;
import com.elcptn.mgmtsvc.validation.OnCreate;
import com.elcptn.mgmtsvc.validation.OnUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Slf4j
@Validated
public class WorkflowController {
    private final WorkflowService workflowService;

    private final WorkflowMapper mapper;

    public WorkflowController(WorkflowService workflowService, WorkflowMapper workflowMapper) {
        this.workflowService = workflowService;
        this.mapper = workflowMapper;
    }

    @Validated(OnCreate.class)
    @PostMapping("/api/workflow")
    public ResponseEntity<WorkflowDto> create(@Valid @RequestBody WorkflowDto workflowDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }
        Workflow workflow = mapper.convertToEntity(workflowDto);
        workflowService.create(workflow);
        return ResponseEntity.ok(mapper.convertToDto((workflow)));
    }

    @Validated(OnUpdate.class)
    @PostMapping("/api/workflow/{id}")
    public ResponseEntity<WorkflowDto> update(@PathVariable String id, @Valid @RequestBody WorkflowDto workflowDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }
        Workflow workflow = mapper.convertToEntity(workflowDto);
        workflowService.create(workflow);
        return ResponseEntity.ok(mapper.convertToDto((workflow)));
    }

}
