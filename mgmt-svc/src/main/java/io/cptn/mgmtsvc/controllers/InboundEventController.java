package io.cptn.mgmtsvc.controllers;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import io.cptn.common.entities.*;
import io.cptn.common.exceptions.NotFoundException;
import io.cptn.common.helpers.FilterParser;
import io.cptn.common.web.ListEntitiesParam;
import io.cptn.mgmtsvc.dto.InboundEventDto;
import io.cptn.mgmtsvc.mappers.InboundEventMapper;
import io.cptn.mgmtsvc.services.InboundEventService;
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

/* @author: kc, created on 4/3/23 */
@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
public class InboundEventController {

    private final InboundEventService inboundEventService;

    private final InboundEventMapper mapper;

    @GetMapping("/api/inbound_event")
    public ResponseEntity<List<InboundEventDto>> list(HttpServletRequest request) {
        ListEntitiesParam listParam = new ListEntitiesParam(request);
        Predicate predicate = getPredicate(listParam);

        List<InboundEventDto> inboundEventDtoList = inboundEventService.getAll(listParam, predicate).stream()
                .map(this::convert).toList();
        long count = inboundEventService.count(predicate);
        return ResponseEntity.ok().header("x-total-count", String.valueOf(count)).body(inboundEventDtoList);
    }

    @Deprecated
    @GetMapping("/api/source/{id}/inbound_event")
    public ResponseEntity<List<InboundEventDto>> list(HttpServletRequest request, @PathVariable UUID id) {
        ListEntitiesParam listParam = new ListEntitiesParam(request);

        BooleanExpression predicate = QInboundEvent.inboundEvent.source().eq(new Source(id));

        List<InboundEventDto> inboundEventDtoList = inboundEventService.getAll(listParam, predicate).stream()
                .map(this::convert).toList();
        long count = inboundEventService.count(predicate);
        return ResponseEntity.ok().header("x-total-count", String.valueOf(count)).body(inboundEventDtoList);
    }

    @GetMapping("/api/inbound_event/{id}")
    public ResponseEntity<InboundEventDto> get(@PathVariable UUID id) {
        InboundEvent inboundEvent = getById(id);
        return ResponseEntity.ok().body(convert(inboundEvent));
    }

    @PostMapping("/api/inbound_event/{id}/resend")
    public ResponseEntity<InboundEventDto> resendEvent(@PathVariable UUID id) {
        InboundEvent inboundEvent = getById(id);
        InboundWriteEvent inboundWriteEvent = inboundEventService.resendEvent(inboundEvent);
        return ResponseEntity.ok().body(convert(inboundWriteEvent));
    }


    private InboundEvent getById(UUID id) {
        Optional<InboundEvent> inboundEventOptional = inboundEventService.getById(id);
        if (inboundEventOptional.isEmpty()) {
            throw new NotFoundException("Event not found with the passed id");
        }

        return inboundEventOptional.get();
    }

    private InboundEventDto convert(InboundEvent inboundEvent) {
        return mapper.toDto(inboundEvent);
    }

    private InboundEventDto convert(InboundWriteEvent inboundEvent) {
        return mapper.toDto(inboundEvent);
    }

    private Predicate getPredicate(ListEntitiesParam param) {
        List<FilterParser.FilterItem> filterItemList = param.getFilters();

        if (filterItemList == null || filterItemList.isEmpty()) {
            return null;
        }

        QInboundEvent qInboundEvent = QInboundEvent.inboundEvent;
        BooleanExpression predicate = qInboundEvent.id.isNotNull();
        for (FilterParser.FilterItem filterItem : filterItemList) {
            if (filterItem.getField().equals("sourceId")) {
                predicate =
                        predicate.and(qInboundEvent.source().id.eq(UUID.fromString(filterItem.getValue())));
                //we only support EQ for now
            } else if (filterItem.getField().equals("state")) {
                try {
                    predicate = predicate.and(qInboundEvent.state.eq(State.valueOf(filterItem.getValue())));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid state value passed in QInboundEvent filter: {}", filterItem.getValue());
                }
            }
        }
        return predicate;
    }
}
