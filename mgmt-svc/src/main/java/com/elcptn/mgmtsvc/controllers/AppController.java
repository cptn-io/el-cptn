package com.elcptn.mgmtsvc.controllers;

import com.elcptn.common.entities.App;
import com.elcptn.common.entities.ScriptedStep;
import com.elcptn.common.helpers.JsonHelper;
import com.elcptn.common.web.ListEntitiesParam;
import com.elcptn.mgmtsvc.dto.AppDto;
import com.elcptn.mgmtsvc.mappers.AppMapper;
import com.elcptn.mgmtsvc.services.AppService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/* @author: kc, created on 4/25/23 */
@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
public class AppController {

    private final AppService appService;

    private final AppMapper mapper;

    @GetMapping("/api/app")
    public ResponseEntity<List<AppDto>> list(HttpServletRequest request) {
        ListEntitiesParam listParam = new ListEntitiesParam(request);
        List<AppDto> appList = appService.getAll(listParam).stream()
                .map(this::convert).collect(Collectors.toList());

        long count = appService.count();
        return ResponseEntity.ok().header("x-total-count", String.valueOf(count)).body(appList);
    }

    @PostMapping("/api/app")
    public ResponseEntity<AppDto> create() {
        App app = appService.createApp();
        return ResponseEntity.ok(convert(app));
    }

    @PostMapping("/api/app/{id}/use")
    public ResponseEntity useApp(@PathVariable UUID id) {
        App app = appService.getAppById(id);
        ScriptedStep createdStep = appService.useApp(app);
        ObjectNode responseNode = JsonHelper.getMapper().createObjectNode();
        responseNode.put("id", createdStep.getId().toString());
        responseNode.put("type", app.getType().name());
        return ResponseEntity.ok(responseNode);
    }

    private AppDto convert(App app) {
        return mapper.toDto(app);
    }
}
