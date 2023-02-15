package com.elcptn.mgmtsvc.controllers;

import com.elcptn.mgmtsvc.dto.EventDto;
import com.elcptn.mgmtsvc.entities.Event;
import com.elcptn.mgmtsvc.entities.Workflow;
import com.elcptn.mgmtsvc.exceptions.NotFoundException;
import com.elcptn.mgmtsvc.mappers.EventMapper;
import com.elcptn.mgmtsvc.services.EventService;
import com.elcptn.mgmtsvc.services.WorkflowService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

/* @author: kc, created on 2/8/23 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class EventController {

    private final WorkflowService workflowService;
    private final EventService eventService;
    private final EventMapper eventMapper;

    @PostMapping("/api/workflow/{workflowId}/event")
    public ResponseEntity<EventDto> createEvent(@PathVariable UUID workflowId,
                                                @Valid @RequestBody JsonNode jsonPayload) {
        Optional<Workflow> workflowOptional = workflowService.getById(workflowId);
        if (workflowOptional.isEmpty()) {
            throw new NotFoundException("Workflow not found with passed ID");
        }

        Event event = new Event();
        event.setPayload(jsonPayload);
        event.setWorkflow(workflowOptional.get());


        return ResponseEntity.ok(convert(eventService.create(event)));
    }

    private EventDto convert(Event event) {
        return eventMapper.eventToEventDto(event);
    }
}
