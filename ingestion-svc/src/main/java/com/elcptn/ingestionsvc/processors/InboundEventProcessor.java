package com.elcptn.ingestionsvc.processors;

import com.elcptn.common.entities.InboundEvent;
import com.elcptn.common.entities.OutboundWriteEvent;
import com.elcptn.common.entities.State;
import com.elcptn.common.repositories.InboundEventRepository;
import com.elcptn.common.repositories.OutboundWriteEventRepository;
import com.elcptn.common.repositories.PipelineRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/* @author: kc, created on 3/9/23 */
@Component
@Slf4j
@AllArgsConstructor
public class InboundEventProcessor {
    private final InboundEventRepository eventRepository;

    private final PipelineRepository pipelineRepository;

    private final OutboundWriteEventRepository outboundWriteEventRepository;

    public void processEvent(InboundEvent event) {
        try {
            dispatchEventToPipelines(event);
            updateEvent(event.getId(), State.COMPLETED);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            updateEvent(event.getId(), State.FAILED);
        }
    }

    private void dispatchEventToPipelines(InboundEvent event) {
        pipelineRepository.findBySource(event.getSource().getId()).forEach(pipeline -> {
            OutboundWriteEvent outboundWriteEvent = new OutboundWriteEvent();
            outboundWriteEvent.setPipeline(pipeline);
            outboundWriteEvent.setPayload(event.getPayload());
            outboundWriteEvent.setInboundEvent(event);
            outboundWriteEventRepository.save(outboundWriteEvent);
        });
    }


    private void updateEvent(UUID eventId, State state) {
        if (eventId == null) {
            return;
        }
        eventRepository.updateEventState(eventId, state);
    }


}
