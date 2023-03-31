package com.elcptn.mgmtsvc.controllers;

import com.elcptn.mgmtsvc.dto.EventDto;
import com.elcptn.mgmtsvc.entities.InboundWriteEvent;
import com.elcptn.mgmtsvc.entities.Source;
import com.elcptn.mgmtsvc.exceptions.NotFoundException;
import com.elcptn.mgmtsvc.mappers.EventMapper;
import com.elcptn.mgmtsvc.services.EventService;
import com.elcptn.mgmtsvc.services.SourceService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

/* @author: kc, created on 2/8/23 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class EventController {

    private final SourceService sourceService;
    private final EventService eventService;
    private final EventMapper eventMapper;

    @PostMapping("/api/source/{sourceId}/event")
    public ResponseEntity<EventDto> createEvent(@PathVariable UUID sourceId,
                                                @RequestBody JsonNode jsonPayload) {
        Optional<Source> sourceOptional = sourceService.getById(sourceId);
        if (sourceOptional.isEmpty()) {
            throw new NotFoundException("Source not found with passed ID");
        }

        InboundWriteEvent event = new InboundWriteEvent();
        event.setPayload(jsonPayload);
        event.setSource(sourceOptional.get());


        return ResponseEntity.ok(convert(eventService.create(event)));
    }

    private EventDto convert(InboundWriteEvent event) {
        return eventMapper.toDto(event);
    }
}
