package com.elcptn.mgmtsvc.services;

import com.elcptn.common.entities.InboundWriteEvent;
import com.elcptn.mgmtsvc.dto.StatusMetricDto;
import com.elcptn.mgmtsvc.repositories.InboundEventRepository;
import com.elcptn.mgmtsvc.repositories.InboundWriteEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

/* @author: kc, created on 2/8/23 */
@Service
@RequiredArgsConstructor
public class InboundEventService extends CommonService {

    private final InboundWriteEventRepository writeEventRepository;

    private final InboundEventRepository eventRepository;

    public InboundWriteEvent create(InboundWriteEvent event) {
        return writeEventRepository.save(event);
    }

    public List<StatusMetricDto> getEventMetrics() {
        return eventRepository.getStatusCountsForEvents(ZonedDateTime.now().minusHours(24));
    }
}
