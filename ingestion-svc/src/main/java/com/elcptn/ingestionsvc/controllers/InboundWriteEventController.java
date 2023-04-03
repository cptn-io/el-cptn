package com.elcptn.ingestionsvc.controllers;

import com.elcptn.common.entities.InboundWriteEvent;
import com.elcptn.common.entities.Source;
import com.elcptn.common.exceptions.NotFoundException;
import com.elcptn.ingestionsvc.dto.InboundWriteEventDto;
import com.elcptn.ingestionsvc.mappers.InboundWriteEventMapper;
import com.elcptn.ingestionsvc.services.InboundWriteEventService;
import com.elcptn.ingestionsvc.services.SourceService;
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
public class InboundWriteEventController {

    private final SourceService sourceService;
    private final InboundWriteEventService inboundEventService;

    private final InboundWriteEventMapper inboundEventMapper;

    @PostMapping("/event/source/{sourceId}")
    public ResponseEntity<InboundWriteEventDto> createEvent(@PathVariable UUID sourceId,
                                                            @RequestBody JsonNode jsonPayload) {
        Optional<Source> sourceOptional = sourceService.getById(sourceId);
        if (sourceOptional.isEmpty()) {
            throw new NotFoundException("Source not found with passed ID");
        }

        InboundWriteEvent event = new InboundWriteEvent();
        event.setPayload(jsonPayload);
        event.setSource(sourceOptional.get());


        return ResponseEntity.ok(convert(inboundEventService.create(event)));
    }

    private InboundWriteEventDto convert(InboundWriteEvent event) {
        return inboundEventMapper.toDto(event);
    }
}
