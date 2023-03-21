package com.elcptn.mgmtsvc.controllers;

/* @author: kc, created on 3/7/23 */

import com.elcptn.mgmtsvc.dto.PipelineDto;
import com.elcptn.mgmtsvc.dto.PipelineTransformationDto;
import com.elcptn.mgmtsvc.entities.Pipeline;
import com.elcptn.mgmtsvc.exceptions.BadRequestException;
import com.elcptn.mgmtsvc.exceptions.NotFoundException;
import com.elcptn.mgmtsvc.helpers.ListEntitiesParam;
import com.elcptn.mgmtsvc.mappers.PipelineMapper;
import com.elcptn.mgmtsvc.services.PipelineService;
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

@RestController
@Slf4j
@RequiredArgsConstructor
public class PipelineController {

    private final PipelineService pipelineService;
    private final PipelineMapper mapper;


    @PostMapping("/api/pipeline")
    public ResponseEntity<PipelineDto> create(@Validated(OnCreate.class) @RequestBody PipelineDto pipelineDto,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        Pipeline pipeline = convert(pipelineDto);

        return ResponseEntity.ok(convert(pipelineService.create(pipeline)));
    }

    @GetMapping("/api/pipeline/{id}")
    public ResponseEntity<PipelineDto> get(@PathVariable UUID id) {
        Pipeline pipeline = getById(id);
        return ResponseEntity.ok(mapper.toDtoWithTransformations(pipeline));
    }

    @GetMapping("/api/pipeline")
    public ResponseEntity<List<PipelineDto>> list(HttpServletRequest request) {
        ListEntitiesParam listParam = new ListEntitiesParam(request);
        List<PipelineDto> sourceList = pipelineService.getAll(listParam).stream()
                .map(this::convert).collect(Collectors.toList());
        long count = pipelineService.count();
        return ResponseEntity.ok().header("x-total-count", String.valueOf(count)).body(sourceList);
    }

    @Validated(OnUpdate.class)
    @PutMapping("/api/pipeline/{id}")
    public ResponseEntity<PipelineDto> update(@PathVariable UUID id, @Valid @RequestBody PipelineDto pipelineDto,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        Pipeline pipeline = getById(id);
        mapper.partialUpdate(pipelineDto, pipeline);

        return ResponseEntity.ok(convert(pipelineService.update(pipeline)));
    }

    @DeleteMapping("/api/pipeline/{id}")
    public ResponseEntity delete(@PathVariable UUID id) {
        Pipeline pipeline = getById(id);
        pipelineService.delete(pipeline);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/pipeline/{id}/link/transformation")
    public ResponseEntity<PipelineDto> addTransformation(@PathVariable UUID id,
                                                         @Valid @RequestBody PipelineTransformationDto pipelineTransformationDto,
                                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        Pipeline pipeline = getById(id);
        pipelineService.addTransformation(pipeline, pipelineTransformationDto.getTransformationId());

        return ResponseEntity.ok(mapper.toDtoWithTransformations(pipeline));
    }

    @PostMapping("/api/pipeline/{id}/unlink/transformation")
    public ResponseEntity<PipelineDto> removeTransformation(@PathVariable UUID id,
                                                            @Valid @RequestBody PipelineTransformationDto pipelineTransformationDto,
                                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        Pipeline pipeline = getById(id);
        pipelineService.removeTransformation(pipeline, pipelineTransformationDto.getTransformationId());

        return ResponseEntity.ok(mapper.toDtoWithTransformations(pipeline));
    }

    private Pipeline getById(UUID id) {
        Optional<Pipeline> pipelineOptional = pipelineService.getById(id);
        if (pipelineOptional.isEmpty()) {
            throw new NotFoundException("Pipeline not found with the passed id");
        }

        return pipelineOptional.get();
    }


    private Pipeline convert(PipelineDto pipelineDto) {
        return mapper.toEntity(pipelineDto);
    }

    private PipelineDto convert(Pipeline pipeline) {
        return mapper.toDto(pipeline);
    }
}
