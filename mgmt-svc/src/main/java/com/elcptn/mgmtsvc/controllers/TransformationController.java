package com.elcptn.mgmtsvc.controllers;

import com.elcptn.common.entities.Transformation;
import com.elcptn.common.exceptions.BadRequestException;
import com.elcptn.common.exceptions.NotFoundException;
import com.elcptn.common.validation.OnCreate;
import com.elcptn.common.validation.OnUpdate;
import com.elcptn.common.web.ListEntitiesParam;
import com.elcptn.mgmtsvc.dto.TransformationDto;
import com.elcptn.mgmtsvc.mappers.TransformationMapper;
import com.elcptn.mgmtsvc.services.TransformationService;
import jakarta.servlet.http.HttpServletRequest;
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
public class TransformationController {

    private final TransformationService transformationService;

    private final TransformationMapper mapper;


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
                .map(this::convert).collect(Collectors.toList());
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
    public ResponseEntity delete(@PathVariable UUID id) {
        Transformation transformation = getById(id);
        transformationService.delete(transformation);
        return ResponseEntity.noContent().build();
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
