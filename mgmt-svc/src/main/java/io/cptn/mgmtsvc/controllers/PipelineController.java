package io.cptn.mgmtsvc.controllers;

/* @author: kc, created on 3/7/23 */

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import io.cptn.common.entities.*;
import io.cptn.common.exceptions.BadRequestException;
import io.cptn.common.exceptions.NotFoundException;
import io.cptn.common.helpers.FilterParser;
import io.cptn.common.validation.OnCreate;
import io.cptn.common.validation.OnUpdate;
import io.cptn.common.web.ListEntitiesParam;
import io.cptn.mgmtsvc.dto.PipelineDto;
import io.cptn.mgmtsvc.mappers.PipelineMapper;
import io.cptn.mgmtsvc.services.PipelineService;
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

        Predicate predicate = getPredicate(listParam);

        List<PipelineDto> sourceList = pipelineService.getAll(listParam, predicate).stream()
                .map(this::convert).toList();
        long count = pipelineService.count(predicate);
        return ResponseEntity.ok().header("x-total-count", String.valueOf(count)).body(sourceList);
    }

    @GetMapping("/api/{entity}/{id}/pipeline")
    public ResponseEntity<List<PipelineDto>> listByReference(HttpServletRequest request,
                                                             @PathVariable String entity,
                                                             @PathVariable UUID id) {
        ListEntitiesParam listParam = new ListEntitiesParam(request);

        BooleanExpression predicate = null;
        QPipeline pipeline = QPipeline.pipeline;
        switch (entity) {
            case "destination":
                predicate = pipeline.destination().eq(new Destination(id));
                break;
            case "source":
                predicate = pipeline.source().eq(new Source(id));
                break;
            case "transformation":
                predicate = pipeline.transformations.contains(new Transformation(id));
                break;
            default:
                predicate = null;
        }

        if (predicate == null) {
            throw new BadRequestException("Not a valid request");
        }


        List<PipelineDto> sourceList = pipelineService.getAll(listParam, predicate).stream()
                .map(this::convert).toList();

        long count = pipelineService.count(predicate);

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
        if (pipelineDto.getTransformations() != null) {
            pipelineService.addTransformations(pipeline, pipelineDto.getTransformations());
        }

        return ResponseEntity.ok(mapper.toDtoWithTransformations(pipelineService.update(pipeline)));
    }

    @DeleteMapping("/api/pipeline/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        Pipeline pipeline = getById(id);
        pipelineService.delete(pipeline);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/pipeline/{id}/run")
    public ResponseEntity<Void> triggerPipelineRun(@PathVariable UUID id) {
        Pipeline pipeline = getById(id);
        pipelineService.runPipeline(pipeline);
        return ResponseEntity.noContent().build();
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

    private Predicate getPredicate(ListEntitiesParam param) {
        List<FilterParser.FilterItem> filterItemList = param.getFilters();

        if (filterItemList == null || filterItemList.isEmpty()) {
            return null;
        }

        QPipeline qPipeline = QPipeline.pipeline;
        BooleanExpression predicate = qPipeline.id.isNotNull();
        for (FilterParser.FilterItem filterItem : filterItemList) {
            if (filterItem.getField().equals("state")) {
                predicate = predicate.and(QPipeline.pipeline.id.in(
                        JPAExpressions.select(QOutboundEvent.outboundEvent.pipeline().id)
                                .from(QOutboundEvent.outboundEvent)
                                .where(QOutboundEvent.outboundEvent.state.eq(State.valueOf(filterItem.getValue())))));

            }
        }
        return predicate;
    }
}
