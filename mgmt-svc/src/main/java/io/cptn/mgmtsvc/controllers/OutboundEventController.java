package io.cptn.mgmtsvc.controllers;

import com.querydsl.core.types.dsl.BooleanExpression;
import io.cptn.common.entities.OutboundEvent;
import io.cptn.common.entities.Pipeline;
import io.cptn.common.entities.QOutboundEvent;
import io.cptn.common.entities.State;
import io.cptn.common.exceptions.NotFoundException;
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
import java.util.stream.Collectors;

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
        List<OutboundEventDto> outboundEventDtoList = outboundEventService.getAll(listParam).stream()
                .map(this::convert).collect(Collectors.toList());
        long count = outboundEventService.count();
        return ResponseEntity.ok().header("x-total-count", String.valueOf(count)).body(outboundEventDtoList);
    }

    @GetMapping("/api/pipeline/{id}/outbound_event")
    public ResponseEntity<List<OutboundEventDto>> list(HttpServletRequest request, @PathVariable UUID id) {
        ListEntitiesParam listParam = new ListEntitiesParam(request);

        BooleanExpression predicate = QOutboundEvent.outboundEvent.pipeline().eq(new Pipeline(id));

        List<OutboundEventDto> outboundEventDtoList = outboundEventService.getAll(listParam, predicate).stream()
                .map(this::convert).collect(Collectors.toList());

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
}
