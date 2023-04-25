package com.elcptn.mgmtsvc.controllers;

import com.elcptn.common.entities.Instance;
import com.elcptn.common.exceptions.BadRequestException;
import com.elcptn.common.validation.OnCreate;
import com.elcptn.mgmtsvc.dto.InstanceDto;
import com.elcptn.mgmtsvc.dto.InstanceInputDto;
import com.elcptn.mgmtsvc.mappers.InstanceMapper;
import com.elcptn.mgmtsvc.services.InstanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/* @author: kc, created on 4/25/23 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class InstanceController {

    private final InstanceService instanceService;

    private final InstanceMapper instanceMapper;

    @PostMapping("/api/instance")
    public ResponseEntity registerInstance(@Validated(OnCreate.class) @RequestBody InstanceInputDto instanceInputDto,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        if (instanceService.getInstance() != null) {
            throw new BadRequestException("Instance is already registered");
        }

        Instance instance = instanceService.registerInstance(instanceInputDto);
        return ResponseEntity.ok(convert(instance));
    }

    @GetMapping("/api/instance")
    public ResponseEntity<InstanceDto> getInstance() {
        Instance instance = instanceService.getInstance();
        return ResponseEntity.ok(convert(instance));
    }


    private InstanceDto convert(Instance instance) {
        return instanceMapper.toDto(instance);
    }
}
