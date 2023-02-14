package com.elcptn.mgmtsvc.controllers;

import com.elcptn.mgmtsvc.dto.OperationDto;
import com.elcptn.mgmtsvc.entities.Operation;
import com.elcptn.mgmtsvc.exceptions.BadRequestException;
import com.elcptn.mgmtsvc.exceptions.NotFoundException;
import com.elcptn.mgmtsvc.helpers.ListEntitiesParam;
import com.elcptn.mgmtsvc.mappers.OperationMapper;
import com.elcptn.mgmtsvc.services.OperationService;
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

/* @author: kc, created on 2/13/23 */
@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
public class OperationController {

    private final OperationService operationService;

    private final OperationMapper mapper;

    @Validated(OnCreate.class)
    @PostMapping("/api/operation")
    public ResponseEntity<OperationDto> createOperation(@Valid @RequestBody OperationDto operationDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        Operation operation = convert(operationDto);

        return ResponseEntity.ok(convert(operationService.create(operation)));
    }

    @Validated(OnUpdate.class)
    @PutMapping("/api/operation/{id}")
    public ResponseEntity<OperationDto> updateOperation(@PathVariable String id,
                                                        @Valid @RequestBody OperationDto operationDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }
        Operation operation = getById(id);
        if (operation.getLocked()) {
            throw new BadRequestException("This operation is in locked state. Changes are not permitted.");
        }
        mapper.updateOperationFromOperationDto(operationDto, operation);

        return ResponseEntity.ok(convert(operationService.update(operation)));
    }

    @PostMapping("/api/operation/{id}/nextVersion")
    public ResponseEntity<OperationDto> nextVersion(@PathVariable String id) {
        Operation operation = getById(id);
        return ResponseEntity.ok(convert(operationService.addNewVersion(operation)));
    }

    @GetMapping("/api/operation")
    public ResponseEntity<List<OperationDto>> getOperations(@RequestParam(name = "appId", required = false) UUID appId,
                                                            HttpServletRequest request) {

        if (appId == null) {
            throw new BadRequestException("appID request param is required");
        }
        ListEntitiesParam listParam = new ListEntitiesParam(request);

        List<OperationDto> workflowList = operationService.getAll(appId, listParam).stream()
                .map(this::convert).collect(Collectors.toList());
        long count = operationService.count(appId);
        return ResponseEntity.ok().header("x-total-count", String.valueOf(count)).body(workflowList);
    }

    @GetMapping("/api/operation/{id}")
    public ResponseEntity<OperationDto> get(@PathVariable String id) {
        Operation operation = getById(id);
        return ResponseEntity.ok(convert((operation)));
    }

    @DeleteMapping("/api/operation/{id}")
    public ResponseEntity deleteOperation(@PathVariable String id) {
        Operation operation = getById(id);
        operationService.delete(operation);
        return ResponseEntity.noContent().build();
    }

    private Operation getById(String id) {
        Optional<Operation> operationOptional = operationService.getById(id);
        if (operationOptional.isEmpty()) {
            throw new NotFoundException("Operation not found with the passed id");
        }

        return operationOptional.get();
    }

    private Operation convert(OperationDto operationDto) {
        return mapper.operationDtoToOperation(operationDto);
    }

    private OperationDto convert(Operation operation) {
        return mapper.operationToOperationDto(operation);
    }
}
