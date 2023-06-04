package io.cptn.mgmtsvc.controllers;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import io.cptn.common.entities.QInboundEvent;
import io.cptn.common.entities.QSource;
import io.cptn.common.entities.Source;
import io.cptn.common.entities.State;
import io.cptn.common.exceptions.BadRequestException;
import io.cptn.common.exceptions.NotFoundException;
import io.cptn.common.helpers.FilterParser;
import io.cptn.common.validation.OnCreate;
import io.cptn.common.validation.OnUpdate;
import io.cptn.common.web.ListEntitiesParam;
import io.cptn.mgmtsvc.dto.SourceDto;
import io.cptn.mgmtsvc.mappers.SourceMapper;
import io.cptn.mgmtsvc.services.SourceService;
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

/* @author: kc, created on 2/7/23 */

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
public class SourceController {

    private final SourceService sourceService;
    private final SourceMapper mapper;


    @PostMapping("/api/source")
    public ResponseEntity<SourceDto> create(@Validated(OnCreate.class) @RequestBody SourceDto sourceDto, BindingResult bindingResult) {
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
        Predicate predicate = getPredicate(listParam);

        List<SourceDto> sourceList = sourceService.getAll(listParam, predicate).stream()
                .map(this::convert).toList();
        long count = sourceService.count(predicate);
        return ResponseEntity.ok().header("x-total-count", String.valueOf(count)).body(sourceList);
    }


    @PutMapping("/api/source/{id}")
    public ResponseEntity<SourceDto> update(@PathVariable UUID id, @Validated(OnUpdate.class) @RequestBody SourceDto sourceDto,
                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        Source source = getById(id);
        mapper.partialUpdate(sourceDto, source);


        return ResponseEntity.ok(convert(sourceService.update(source)));
    }

    @PutMapping("/api/source/{id}/rotateKeys")
    public ResponseEntity<SourceDto> rotateKeys(@PathVariable UUID id) {
        Source source = getById(id);

        return ResponseEntity.ok(convert(sourceService.rotateKeys(source)));
    }

    @DeleteMapping("/api/source/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        Source source = getById(id);
        try {
            sourceService.delete(source);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Unable to delete Source as it is being used by other entities");
        }
        return ResponseEntity.noContent().build();
    }

    private Source getById(UUID id) {
        Optional<Source> sourceOptional = sourceService.getById(id);
        if (sourceOptional.isEmpty()) {
            throw new NotFoundException("Source not found with the passed id");
        }

        return sourceOptional.get();
    }

    private Source convert(SourceDto sourceDto) {
        return mapper.toEntity(sourceDto);
    }

    private SourceDto convert(Source source) {
        return mapper.toDto(source);
    }

    private Predicate getPredicate(ListEntitiesParam param) {
        List<FilterParser.FilterItem> filterItemList = param.getFilters();

        if (filterItemList == null || filterItemList.isEmpty()) {
            return null;
        }

        QSource qSource = QSource.source;
        BooleanExpression predicate = qSource.id.isNotNull();
        for (FilterParser.FilterItem filterItem : filterItemList) {
            if (filterItem.getField().equals("state")) {
                predicate = predicate.and(QSource.source.id.in(
                        JPAExpressions.select(QInboundEvent.inboundEvent.source().id)
                                .from(QInboundEvent.inboundEvent)
                                .where(QInboundEvent.inboundEvent.state.eq(State.valueOf(filterItem.getValue())))));
            }
        }
        return predicate;
    }
}
