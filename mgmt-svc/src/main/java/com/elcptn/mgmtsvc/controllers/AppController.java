package com.elcptn.mgmtsvc.controllers;

import com.elcptn.mgmtsvc.dto.AppDto;
import com.elcptn.mgmtsvc.entities.App;
import com.elcptn.mgmtsvc.exceptions.BadRequestException;
import com.elcptn.mgmtsvc.exceptions.NotFoundException;
import com.elcptn.mgmtsvc.helpers.ListEntitiesParam;
import com.elcptn.mgmtsvc.mappers.AppMapper;
import com.elcptn.mgmtsvc.services.AppService;
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

/* @author: kc, created on 2/9/23 */
@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
public class AppController {
    private final AppService appService;

    private final AppMapper appMapper;

    @Validated(OnCreate.class)
    @PostMapping("/api/app")
    public ResponseEntity<AppDto> create(@Valid @RequestBody AppDto appDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        App app = convert(appDto);

        return ResponseEntity.ok(convert(appService.create(app)));
    }

    @GetMapping("/api/app/{id}")
    public ResponseEntity<AppDto> get(@PathVariable UUID id) {
        App app = getById(id);
        return ResponseEntity.ok(convert((app)));
    }

    @GetMapping("/api/app")
    public ResponseEntity<List<AppDto>> list(HttpServletRequest request) {
        ListEntitiesParam listParam = new ListEntitiesParam(request);
        List<AppDto> appList = appService.getAll(listParam).stream()
                .map(this::convert).collect(Collectors.toList());
        long count = appService.count();
        return ResponseEntity.ok().header("x-total-count", String.valueOf(count)).body(appList);
    }

    @Validated(OnUpdate.class)
    @PutMapping("/api/app/{id}")
    public ResponseEntity<AppDto> update(@PathVariable UUID id, @Valid @RequestBody AppDto appDto,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        App app = getById(id);
        appMapper.updateAppFromAppDto(appDto, app);


        return ResponseEntity.ok(convert(appService.update(app)));
    }

    @DeleteMapping("/api/app/{id}")
    public ResponseEntity delete(@PathVariable UUID id) {
        App app = getById(id);
        appService.delete(app);
        return ResponseEntity.noContent().build();
    }

    private App getById(UUID id) {
        Optional<App> appOptional = appService.getById(id);
        if (appOptional.isEmpty()) {
            throw new NotFoundException("App not found with the passed id");
        }

        return appOptional.get();
    }

    private App convert(AppDto appDto) {
        return appMapper.appDtoToApp(appDto);
    }

    private AppDto convert(App app) {
        return appMapper.appToAppDto(app);
    }
}
