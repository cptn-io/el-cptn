package com.elcptn.mgmtsvc.controllers;

import com.elcptn.mgmtsvc.dto.ActionDto;
import com.elcptn.mgmtsvc.entities.Action;
import com.elcptn.mgmtsvc.exceptions.BadRequestException;
import com.elcptn.mgmtsvc.exceptions.NotFoundException;
import com.elcptn.mgmtsvc.helpers.ListEntitiesParam;
import com.elcptn.mgmtsvc.mappers.ActionMapper;
import com.elcptn.mgmtsvc.services.ActionService;
import com.elcptn.mgmtsvc.validation.OnCreate;
import com.elcptn.mgmtsvc.validation.OnReference;
import com.elcptn.mgmtsvc.validation.OnUpdate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/* @author: kc, created on 2/16/23 */
@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
public class ActionController {

    private final ActionService actionService;

    private final ActionMapper mapper;

    @Validated(OnCreate.class)
    @PostMapping("/api/action")
    public ResponseEntity<ActionDto> create(@Valid @RequestBody ActionDto actionDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        Action action = actionService.create(convert(actionDto));

        return ResponseEntity.ok(convert(action));
    }

    @GetMapping("/api/action")
    public ResponseEntity<List<ActionDto>> list(HttpServletRequest request) {
        ListEntitiesParam listParam = new ListEntitiesParam(request);
        List<ActionDto> actionList = actionService.getAll(listParam).stream()
                .map(this::convert).collect(Collectors.toList());
        long count = actionService.count();
        return ResponseEntity.ok().header("x-total-count", String.valueOf(count)).body(actionList);
    }

    @GetMapping("/api/action/{id}")
    public ResponseEntity<ActionDto> get(@PathVariable UUID id) {
        Action action = getById(id);
        return ResponseEntity.ok(convert((action)));
    }


    @Validated(OnUpdate.class)
    @PutMapping("/api/action/{id}")
    public ResponseEntity<ActionDto> updateAction(@PathVariable UUID id, @Valid @RequestBody ActionDto actionDto,
                                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }
        Action action = getById(id);

        mapper.updateActionFromActionDto(actionDto, action);

        return ResponseEntity.ok(convert(actionService.update(action)));
    }

    @Validated(OnReference.class)
    @PostMapping("/api/action/{id}/source")
    public ResponseEntity<ActionDto> addActionToSource(@PathVariable UUID id, @Valid @RequestBody ActionDto actionDto,
                                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        Action action = getById(id);
        mapper.mergeActionSourcesfromActionDto(actionDto, action);
        return ResponseEntity.ok(convert(actionService.update(action)));
    }


    @DeleteMapping("/api/action/{id}")
    public ResponseEntity delete(@PathVariable UUID id) {
        Action action = getById(id);
        actionService.delete(action);
        return ResponseEntity.noContent().build();
    }

    private Action getById(UUID id) {
        Optional<Action> actionOptional = actionService.getById(id);
        if (actionOptional.isEmpty()) {
            throw new NotFoundException("Action not found with the passed id");
        }

        return actionOptional.get();
    }

    private Action convert(ActionDto actionDto) {
        return mapper.actionDtoToAction(actionDto);
    }

    private ActionDto convert(Action action) {
        return mapper.actionToActionDto(action);
    }
}
