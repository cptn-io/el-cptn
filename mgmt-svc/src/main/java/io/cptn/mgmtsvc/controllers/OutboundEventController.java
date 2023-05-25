package io.cptn.mgmtsvc.controllers;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import io.cptn.common.entities.OutboundEvent;
import io.cptn.common.entities.Pipeline;
import io.cptn.common.entities.QOutboundEvent;
import io.cptn.common.entities.State;
import io.cptn.common.exceptions.NotFoundException;
import io.cptn.common.helpers.FilterParser;
import io.cptn.common.web.ListEntitiesParam;
import io.cptn.mgmtsvc.dto.OutboundEventDto;
import io.cptn.mgmtsvc.mappers.OutboundEventMapper;
import io.cptn.mgmtsvc.services.OutboundEventService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/* @author: kc, created on 4/4/23 */
@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
public class OutboundEventController {

    private final OutboundEventService outboundEventService;

    private final OutboundEventMapper mapper;

    @GetMapping("/api/outbound_event")
    public ResponseEntity<List<OutboundEventDto>> list(HttpServletRequest request) {
        ListEntitiesParam listParam = new ListEntitiesParam(request);

        Predicate predicate = getPredicate(listParam);
        List<OutboundEventDto> outboundEventDtoList = outboundEventService.getAll(listParam, predicate).stream()
                .map(this::convert).toList();
        long count = outboundEventService.count(predicate);
        return ResponseEntity.ok().header("x-total-count", String.valueOf(count)).body(outboundEventDtoList);
    }

    @Deprecated(forRemoval = true, since = "0.2.0")
    @GetMapping("/api/pipeline/{id}/outbound_event")
    public ResponseEntity<List<OutboundEventDto>> list(HttpServletRequest request, @PathVariable UUID id) {
        ListEntitiesParam listParam = new ListEntitiesParam(request);

        BooleanExpression predicate = QOutboundEvent.outboundEvent.pipeline().eq(new Pipeline(id));

        List<OutboundEventDto> outboundEventDtoList = outboundEventService.getAll(listParam, predicate).stream()
                .map(this::convert).toList();

        long count = outboundEventService.count(predicate);
        return ResponseEntity.ok().header("x-total-count", String.valueOf(count)).body(outboundEventDtoList);
    }

    @GetMapping("/api/outbound_event/{id}")
    public ResponseEntity<OutboundEventDto> get(@PathVariable UUID id) {
        OutboundEvent outboundEvent = getById(id);
        return ResponseEntity.ok().body(convert(outboundEvent));
    }

    @PostMapping("/api/outbound_event/{id}/requeue")
    public ResponseEntity<OutboundEventDto> requeue(@PathVariable UUID id) {
        OutboundEvent outboundEvent = getById(id);
        outboundEvent.setState(State.QUEUED);
        outboundEventService.save(outboundEvent);
        return ResponseEntity.ok().body(convert(outboundEvent));
    }

    @PostMapping("/api/outbound_event/pipeline/{pipelineId}/requeue")
    public ResponseEntity<Void> requeueAllFailedInPipeline(@PathVariable UUID pipelineId) {
        outboundEventService.requeueFailedEventsInPipeline(pipelineId);
        return ResponseEntity.noContent().build();
    }

    private OutboundEvent getById(UUID id) {
        Optional<OutboundEvent> outboundEventOptional = outboundEventService.getById(id);
        if (outboundEventOptional.isEmpty()) {
            throw new NotFoundException("Event not found with the passed id");
        }

        return outboundEventOptional.get();
    }

    private OutboundEventDto convert(OutboundEvent outboundEvent) {
        return mapper.toDto(outboundEvent);
    }

    private Predicate getPredicate(ListEntitiesParam param) {
        List<FilterParser.FilterItem> filterItemList = param.getFilters();

        if (filterItemList == null || filterItemList.isEmpty()) {
            return null;
        }
        QOutboundEvent qOutboundEvent = QOutboundEvent.outboundEvent;
        BooleanExpression predicate = qOutboundEvent.id.isNotNull();
        for (FilterParser.FilterItem filterItem : filterItemList) {
            if (filterItem.getField().equals("pipelineId")) {
                predicate = predicate.and(qOutboundEvent.pipeline().id.eq(UUID.fromString(filterItem.getValue())));
                //we only support EQ for now
            } else if (filterItem.getField().equals("state")) {
                try {
                    predicate = predicate.and(qOutboundEvent.state.eq(State.valueOf(filterItem.getValue())));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid state value passed in OutboundEvent filter: {}", filterItem.getValue());
                }
            }
        }
        return predicate;
    }
}
