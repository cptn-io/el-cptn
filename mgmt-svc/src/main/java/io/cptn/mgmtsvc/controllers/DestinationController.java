package io.cptn.mgmtsvc.controllers;

import io.cptn.common.entities.Destination;
import io.cptn.common.exceptions.BadRequestException;
import io.cptn.common.exceptions.NotFoundException;
import io.cptn.common.validation.OnCreate;
import io.cptn.common.validation.OnUpdate;
import io.cptn.common.web.ListEntitiesParam;
import io.cptn.mgmtsvc.dto.DestinationDto;
import io.cptn.mgmtsvc.mappers.DestinationMapper;
import io.cptn.mgmtsvc.services.DestinationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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

    @PostMapping("/api/destination")
    public ResponseEntity<DestinationDto> create(@Validated(OnCreate.class) @RequestBody DestinationDto destinationDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        Destination destination = destinationService.create(convert(destinationDto));

        return ResponseEntity.ok(convert(destination));
    }

    @GetMapping("/api/destination")
    public ResponseEntity<List<DestinationDto>> list(HttpServletRequest request) {
        ListEntitiesParam listParam = new ListEntitiesParam(request);
        List<DestinationDto> destinationDtoList = destinationService.getAll(listParam).stream()
                .map(mapper::toDto).collect(Collectors.toList());
        long count = destinationService.count();
        return ResponseEntity.ok().header("x-total-count", String.valueOf(count)).body(destinationDtoList);
    }

    @GetMapping("/api/destination/{id}")
    public ResponseEntity<DestinationDto> get(@PathVariable UUID id) {
        Destination destination = getById(id);
        return ResponseEntity.ok(convert((destination)));
    }


    @PutMapping("/api/destination/{id}")
    public ResponseEntity<DestinationDto> updateAction(@PathVariable UUID id, @Validated(OnUpdate.class) @RequestBody DestinationDto destinationDto,
                                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        Destination destination = getById(id);

        mapper.partialUpdate(destinationDto, destination);

        return ResponseEntity.ok(convert(destinationService.update(destination, destinationDto.getConfig() != null)));
    }


    @DeleteMapping("/api/destination/{id}")
    public ResponseEntity delete(@PathVariable UUID id) {
        Destination destination = getById(id);
        try {
            destinationService.delete(destination);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Unable to delete destination as it is being used by other entities");
        }
        return ResponseEntity.noContent().build();
    }

    private Destination getById(UUID id) {
        Optional<Destination> destinationOptional = destinationService.getById(id);
        if (destinationOptional.isEmpty()) {
            throw new NotFoundException("Action not found with the passed id");
        }

        return destinationOptional.get();
    }

    private Destination convert(DestinationDto destinationDto) {
        return mapper.toEntity(destinationDto);
    }

    private DestinationDto convert(Destination destination) {
        return mapper.toDto(destination);
    }
}
