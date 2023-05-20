package io.cptn.mgmtsvc.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cptn.common.entities.App;
import io.cptn.common.entities.ScriptedStep;
import io.cptn.common.exceptions.WebApplicationException;
import io.cptn.common.helpers.JsonHelper;
import io.cptn.common.web.ListEntitiesParam;
import io.cptn.mgmtsvc.dto.AppDto;
import io.cptn.mgmtsvc.mappers.AppMapper;
import io.cptn.mgmtsvc.services.AppService;
import io.cptn.mgmtsvc.services.AppSynchronizer;
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

    private final AppSynchronizer appSynchronizer;

    @GetMapping("/api/app")
    public ResponseEntity<List<AppDto>> list(HttpServletRequest request) {
        ListEntitiesParam listParam = new ListEntitiesParam(request);
        List<AppDto> appList = appService.getAll(listParam).stream()
                .map(this::convert).collect(Collectors.toList());

        long count = appService.count();
        return ResponseEntity.ok().header("x-total-count", String.valueOf(count)).body(appList);
    }

    @PostMapping("/api/app/{id}/use")
    public ResponseEntity<JsonNode> useApp(@PathVariable UUID id) {
        App app = appService.getAppById(id);
        ScriptedStep createdStep = appService.useApp(app);
        ObjectNode responseNode = JsonHelper.getMapper().createObjectNode();
        responseNode.put("id", createdStep.getId().toString());
        responseNode.put("type", app.getType().name());
        return ResponseEntity.ok(responseNode);
    }

    @PostMapping("/api/app/sync")
    public ResponseEntity<Void> syncApps() {
        try {
            appSynchronizer.syncWithRepository();
        } catch (Exception e) {
            log.error("Unable to sync apps - " + e.getMessage(), e);
            throw new WebApplicationException("Unable to sync with remote app repository. Your service must have " +
                    "access to internet to sync applications.");
        }
        return ResponseEntity.ok().build();
    }

    private AppDto convert(App app) {
        return mapper.toDto(app);
    }
}
