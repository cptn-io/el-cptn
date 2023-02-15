package com.elcptn.mgmtsvc.controllers;

import com.elcptn.mgmtsvc.dto.WorkflowDto;
import com.elcptn.mgmtsvc.entities.Workflow;
import com.elcptn.mgmtsvc.exceptions.BadRequestException;
import com.elcptn.mgmtsvc.exceptions.NotFoundException;
import com.elcptn.mgmtsvc.helpers.ListEntitiesParam;
import com.elcptn.mgmtsvc.mappers.WorkflowMapper;
import com.elcptn.mgmtsvc.services.WorkflowService;
import com.elcptn.mgmtsvc.validation.OnCreate;
import com.elcptn.mgmtsvc.validation.OnUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/* @author: kc, created on 2/7/23 */

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;
    private final WorkflowMapper mapper;

    @Validated(OnCreate.class)
    @PostMapping("/api/workflow")
    public ResponseEntity<WorkflowDto> create(@Valid @RequestBody WorkflowDto workflowDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        Workflow workflow = convert(workflowDto);

        return ResponseEntity.ok(convert(workflowService.create(workflow)));
    }

    @GetMapping("/api/workflow/{id}")
    public ResponseEntity<WorkflowDto> get(@PathVariable UUID id) {
        Workflow workflow = getById(id);
        return ResponseEntity.ok(convert((workflow)));
    }

    @GetMapping("/api/workflow")
    public ResponseEntity<List<WorkflowDto>> list(HttpServletRequest request) {
        ListEntitiesParam listParam = new ListEntitiesParam(request);
        List<WorkflowDto> workflowList = workflowService.getAll(listParam).stream()
                .map(this::convert).collect(Collectors.toList());
        long count = workflowService.count();
        return ResponseEntity.ok().header("x-total-count", String.valueOf(count)).body(workflowList);
    }

    @Validated(OnUpdate.class)
    @PutMapping("/api/workflow/{id}")
    public ResponseEntity<WorkflowDto> update(@PathVariable UUID id, @Valid @RequestBody WorkflowDto workflowDto,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        Workflow workflow = getById(id);
        mapper.updateWorkflowFromWorkflowDto(workflowDto, workflow);


        return ResponseEntity.ok(convert(workflowService.update(workflow)));
    }

    @PutMapping("/api/workflow/{id}/rotateKeys")
    public ResponseEntity<WorkflowDto> rotateKeys(@PathVariable UUID id) {
        Workflow workflow = getById(id);

        return ResponseEntity.ok(convert(workflowService.rotateKeys(workflow)));
    }

    @DeleteMapping("/api/workflow/{id}")
    public ResponseEntity delete(@PathVariable UUID id) {
        Workflow workflow = getById(id);
        workflowService.delete(workflow);
        return ResponseEntity.noContent().build();
    }

    private Workflow getById(UUID id) {
        Optional<Workflow> workflowOptional = workflowService.getById(id);
        if (workflowOptional.isEmpty()) {
            throw new NotFoundException("Workflow not found with the passed id");
        }

        return workflowOptional.get();
    }

    private Workflow convert(WorkflowDto workflowDto) {
        return mapper.workflowDtoToWorkflow(workflowDto);
    }

    private WorkflowDto convert(Workflow workflow) {
        return mapper.workflowToWorkflowDto(workflow);
    }
}
