package com.elcptn.mgmtsvc.scheduler.processors;

import com.elcptn.mgmtsvc.entities.Event;
import com.elcptn.mgmtsvc.entities.OutboundWriteEvent;
import com.elcptn.mgmtsvc.entities.Pipeline;
import com.elcptn.mgmtsvc.entities.State;
import com.elcptn.mgmtsvc.repositories.EventRepository;
import com.elcptn.mgmtsvc.repositories.OutboundWriteEventRepository;
import com.elcptn.mgmtsvc.repositories.PipelineRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/* @author: kc, created on 3/9/23 */
@Component
@Slf4j
@AllArgsConstructor
public class InboundEventProcessor {

    private final ObjectMapper mapper = new ObjectMapper();
    private final EventRepository eventRepository;

    private final PipelineRepository pipelineRepository;

    private final OutboundWriteEventRepository outboundWriteEventRepository;

    public void processEvent(Event event) {
        try {
            dispatchEventToPipelines(event);
            updateEvent(event.getId(), State.COMPLETED);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            updateEvent(event.getId(), State.FAILED);
        }
    }

    private void dispatchEventToPipelines(Event event) {
        pipelineRepository.findActivePipelinesBySource(event.getSource().getId()).forEach(pipeline -> {
            OutboundWriteEvent outboundWriteEvent = new OutboundWriteEvent();
            outboundWriteEvent.setPipeline(pipeline);
            outboundWriteEvent.setPayload(event.getPayload());
            outboundWriteEvent.setSteps(getPipelineSteps(pipeline));
            outboundWriteEventRepository.save(outboundWriteEvent);
        });
    }

    private JsonNode getPipelineSteps(Pipeline pipeline) {
        ArrayNode stepsArray = mapper.createArrayNode();
        //finally add destination
        stepsArray.add(mapper.createObjectNode().put("id", pipeline.getDestination().getId().toString()).put("type",
                "destination").put("version", pipeline.getDestination().getVersion()));
        return stepsArray;
    }

    private void updateEvent(UUID eventId, State state) {
        if (eventId == null) {
            return;
        }
        eventRepository.updateEventState(eventId, state);
    }


}
