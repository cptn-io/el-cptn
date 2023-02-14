package com.elcptn.mgmtsvc.controllers;

import com.elcptn.mgmtsvc.dto.OperationDto;
import com.elcptn.mgmtsvc.entities.Operation;
import com.elcptn.mgmtsvc.exceptions.BadRequestException;
import com.elcptn.mgmtsvc.mappers.OperationMapper;
import com.elcptn.mgmtsvc.services.OperationService;
import com.elcptn.mgmtsvc.validation.OnCreate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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

    private Operation convert(OperationDto operationDto) {
        return mapper.operationDtoToOperation(operationDto);
    }

    private OperationDto convert(Operation operation) {
        return mapper.operationToOperationDto(operation);
    }
}
