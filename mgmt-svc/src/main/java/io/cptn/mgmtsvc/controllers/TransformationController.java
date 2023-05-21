package io.cptn.mgmtsvc.controllers;

import io.cptn.common.entities.App;
import io.cptn.common.entities.Transformation;
import io.cptn.common.exceptions.BadRequestException;
import io.cptn.common.exceptions.NotFoundException;
import io.cptn.common.validation.OnCreate;
import io.cptn.common.validation.OnUpdate;
import io.cptn.common.web.ListEntitiesParam;
import io.cptn.mgmtsvc.dto.ExportedAppDto;
import io.cptn.mgmtsvc.dto.TransformationDto;
import io.cptn.mgmtsvc.mappers.ExportedAppMapper;
import io.cptn.mgmtsvc.mappers.TransformationMapper;
import io.cptn.mgmtsvc.services.TransformationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/* @author: kc, created on 2/16/23 */
@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
public class TransformationController {

    private final TransformationService transformationService;

    private final TransformationMapper mapper;

    private final ExportedAppMapper exportedAppMapper;


    @PostMapping("/api/transformation")
    public ResponseEntity<TransformationDto> create(@Validated(OnCreate.class) @RequestBody TransformationDto transformationDto,
                                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        Transformation transformation = transformationService.create(convert(transformationDto));

        return ResponseEntity.ok(convert(transformation));
    }

    @GetMapping("/api/transformation")
    public ResponseEntity<List<TransformationDto>> list(HttpServletRequest request) {
        ListEntitiesParam listParam = new ListEntitiesParam(request);
        List<TransformationDto> transformationDtoList = transformationService.getAll(listParam).stream()
                .map(this::convert).toList();
        long count = transformationService.count();
        return ResponseEntity.ok().header("x-total-count", String.valueOf(count)).body(transformationDtoList);
    }

    @GetMapping("/api/transformation/{id}")
    public ResponseEntity<TransformationDto> get(@PathVariable UUID id) {
        Transformation transformation = getById(id);
        return ResponseEntity.ok(convert((transformation)));
    }


    @PutMapping("/api/transformation/{id}")
    public ResponseEntity<TransformationDto> updateAction(@PathVariable UUID id,
                                                          @Validated(OnUpdate.class) @RequestBody TransformationDto transformationDto,
                                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        Transformation transformation = getById(id);
        mapper.partialUpdate(transformationDto, transformation);

        return ResponseEntity.ok(convert(transformationService.update(transformation)));
    }


    @DeleteMapping("/api/transformation/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        Transformation transformation = getById(id);
        try {
            transformationService.delete(transformation);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Unable to delete transformation as it is being used by other entities");
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/transformation/{id}/export")
    public ResponseEntity<ExportedAppDto> downloadAsApp(@PathVariable UUID id) {
        Transformation transformation = getById(id);

        if (transformation == null) {
            throw new NotFoundException("Transformation not found with the passed id");
        }

        App app = transformationService.exportAsApp(transformation);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Disposition", "attachment; filename=" + app.getKey() + ".json");

        return ResponseEntity.ok().headers(httpHeaders).body(exportedAppMapper.toDto(app));
    }

    private Transformation getById(UUID id) {
        Optional<Transformation> transformationOptional = transformationService.getById(id);
        if (transformationOptional.isEmpty()) {
            throw new NotFoundException("Transformation not found with the passed id");
        }

        return transformationOptional.get();
    }

    private Transformation convert(TransformationDto transformationDto) {
        return mapper.toEntity(transformationDto);
    }

    private TransformationDto convert(Transformation transformation) {
        return mapper.toDto(transformation);
    }
}
