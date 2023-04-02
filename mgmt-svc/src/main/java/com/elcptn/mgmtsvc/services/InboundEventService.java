package com.elcptn.mgmtsvc.services;

import com.elcptn.common.entities.InboundWriteEvent;
import com.elcptn.common.pojos.StatusMetric;
import com.elcptn.common.repositories.InboundEventRepository;
import com.elcptn.common.repositories.InboundWriteEventRepository;
import com.elcptn.mgmtsvc.dto.StatusMetricDto;
import com.elcptn.mgmtsvc.mappers.StatusMetricMapper;
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

    private final StatusMetricMapper statusMetricMapper;

    public InboundWriteEvent create(InboundWriteEvent event) {
        return writeEventRepository.save(event);
    }

    public List<StatusMetricDto> getEventMetrics() {
        List<StatusMetric> statusMetrics = eventRepository.getStatusCountsForEvents(ZonedDateTime.now().minusHours(24));
        return statusMetricMapper.toDtoList(statusMetrics);
    }
}
