package com.elcptn.mgmtsvc.controllers;

import com.elcptn.common.entities.App;
import com.elcptn.common.web.ListEntitiesParam;
import com.elcptn.mgmtsvc.dto.AppDto;
import com.elcptn.mgmtsvc.mappers.AppMapper;
import com.elcptn.mgmtsvc.services.AppService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
        return ResponseEntity.ok(appList);
    }

    @PostMapping("/api/app")
    public ResponseEntity<AppDto> create() {
        App app = appService.createApp();
        return ResponseEntity.ok(convert(app));
    }

    private AppDto convert(App app) {
        return mapper.toDto(app);
    }
}
