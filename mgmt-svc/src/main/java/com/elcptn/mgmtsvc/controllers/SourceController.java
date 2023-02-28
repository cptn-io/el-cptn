package com.elcptn.mgmtsvc.controllers;

import com.elcptn.mgmtsvc.dto.SourceDto;
import com.elcptn.mgmtsvc.entities.Source;
import com.elcptn.mgmtsvc.exceptions.BadRequestException;
import com.elcptn.mgmtsvc.exceptions.NotFoundException;
import com.elcptn.mgmtsvc.helpers.ListEntitiesParam;
import com.elcptn.mgmtsvc.mappers.SourceMapper;
import com.elcptn.mgmtsvc.services.SourceService;
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

/* @author: kc, created on 2/7/23 */

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
public class SourceController {

    private final SourceService sourceService;
    private final SourceMapper mapper;

    @Validated(OnCreate.class)
    @PostMapping("/api/source")
    public ResponseEntity<SourceDto> create(@Valid @RequestBody SourceDto sourceDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        Source source = convert(sourceDto);

        return ResponseEntity.ok(convert(sourceService.create(source)));
    }

    @GetMapping("/api/source/{id}")
    public ResponseEntity<SourceDto> get(@PathVariable UUID id) {
        Source source = getById(id);
        return ResponseEntity.ok(convert((source)));
    }

    @GetMapping("/api/source")
    public ResponseEntity<List<SourceDto>> list(HttpServletRequest request) {
        ListEntitiesParam listParam = new ListEntitiesParam(request);
        List<SourceDto> sourceList = sourceService.getAll(listParam).stream()
                .map(this::convert).collect(Collectors.toList());
        long count = sourceService.count();
        return ResponseEntity.ok().header("x-total-count", String.valueOf(count)).body(sourceList);
    }

    @Validated(OnUpdate.class)
    @PutMapping("/api/source/{id}")
    public ResponseEntity<SourceDto> update(@PathVariable UUID id, @Valid @RequestBody SourceDto sourceDto,
                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        Source source = getById(id);
        mapper.updateSourceFromSourceDto(sourceDto, source);


        return ResponseEntity.ok(convert(sourceService.update(source)));
    }

    @PutMapping("/api/source/{id}/rotateKeys")
    public ResponseEntity<SourceDto> rotateKeys(@PathVariable UUID id) {
        Source Source = getById(id);

        return ResponseEntity.ok(convert(sourceService.rotateKeys(Source)));
    }

    @DeleteMapping("/api/source/{id}")
    public ResponseEntity delete(@PathVariable UUID id) {
        Source Source = getById(id);
        sourceService.delete(Source);
        return ResponseEntity.noContent().build();
    }

    private Source getById(UUID id) {
        Optional<Source> SourceOptional = sourceService.getById(id);
        if (SourceOptional.isEmpty()) {
            throw new NotFoundException("Source not found with the passed id");
        }

        return SourceOptional.get();
    }

    private Source convert(SourceDto SourceDto) {
        return mapper.sourceDtoToSource(SourceDto);
    }

    private SourceDto convert(Source Source) {
        return mapper.sourceToSourceDto(Source);
    }
}
