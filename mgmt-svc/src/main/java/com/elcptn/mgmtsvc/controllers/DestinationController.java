package com.elcptn.mgmtsvc.controllers;

import com.elcptn.mgmtsvc.dto.DestinationDto;
import com.elcptn.mgmtsvc.entities.Destination;
import com.elcptn.mgmtsvc.exceptions.BadRequestException;
import com.elcptn.mgmtsvc.exceptions.NotFoundException;
import com.elcptn.mgmtsvc.helpers.ListEntitiesParam;
import com.elcptn.mgmtsvc.mappers.DestinationMapper;
import com.elcptn.mgmtsvc.services.DestinationService;
import com.elcptn.mgmtsvc.validation.OnCreate;
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
public class DestinationController {

    private final DestinationService destinationService;

    private final DestinationMapper mapper;

    @Validated(OnCreate.class)
    @PostMapping("/api/destination")
    public ResponseEntity<DestinationDto> create(@Valid @RequestBody DestinationDto destinationDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        Destination destination = destinationService.create(convert(destinationDto));

        return ResponseEntity.ok(convert(destination));
    }

    @GetMapping("/api/destination")
    public ResponseEntity<List<DestinationDto>> list(HttpServletRequest request) {
        ListEntitiesParam listParam = new ListEntitiesParam(request);
        List<DestinationDto> actionList = destinationService.getAll(listParam).stream()
                .map(this::convert).collect(Collectors.toList());
        long count = destinationService.count();
        return ResponseEntity.ok().header("x-total-count", String.valueOf(count)).body(actionList);
    }

    @GetMapping("/api/destination/{id}")
    public ResponseEntity<DestinationDto> get(@PathVariable UUID id) {
        Destination destination = getById(id);
        return ResponseEntity.ok(convert((destination)));
    }


    @Validated(OnUpdate.class)
    @PutMapping("/api/destination/{id}")
    public ResponseEntity<DestinationDto> updateAction(@PathVariable UUID id, @Valid @RequestBody DestinationDto destinationDto,
                                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }
        Destination destination = getById(id);

        mapper.partialUpdate(destinationDto, destination);

        return ResponseEntity.ok(convert(destinationService.update(destination)));
    }


    @DeleteMapping("/api/destination/{id}")
    public ResponseEntity delete(@PathVariable UUID id) {
        Destination destination = getById(id);
        destinationService.delete(destination);
        return ResponseEntity.noContent().build();
    }

    private Destination getById(UUID id) {
        Optional<Destination> actionOptional = destinationService.getById(id);
        if (actionOptional.isEmpty()) {
            throw new NotFoundException("Action not found with the passed id");
        }

        return actionOptional.get();
    }

    private Destination convert(DestinationDto destinationDto) {
        return mapper.toEntity(destinationDto);
    }

    private DestinationDto convert(Destination destination) {
        return mapper.toDto(destination);
    }
}
