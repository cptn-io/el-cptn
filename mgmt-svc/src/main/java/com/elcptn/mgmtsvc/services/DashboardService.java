package com.elcptn.mgmtsvc.services;

import com.elcptn.mgmtsvc.dto.DashboardMetricsDto;
import com.elcptn.mgmtsvc.dto.StatusMetricDto;
import com.elcptn.mgmtsvc.repositories.DestinationRepository;
import com.elcptn.mgmtsvc.repositories.EventRepository;
import com.elcptn.mgmtsvc.repositories.PipelineRepository;
import com.elcptn.mgmtsvc.repositories.SourceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

/* @author: kc, created on 3/9/23 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final PipelineRepository pipelineRepository;

    private final SourceRepository sourceRepository;

    private final DestinationRepository destinationRepository;

    private final EventRepository eventRepository;

    public DashboardMetricsDto getMetrics() {
        DashboardMetricsDto metricsDto = new DashboardMetricsDto();

        //inbound event metrics
        List<StatusMetricDto> inboundEventMetrics = eventRepository.getStatusCountsForCollectionRun(ZonedDateTime.now().minusHours(24));
        metricsDto.setInbound(inboundEventMetrics);

        //entity counts
        ObjectNode entityStats = mapper.createObjectNode();
        entityStats.put("pipelines", pipelineRepository.count());
        entityStats.put("sources", sourceRepository.count());
        entityStats.put("destinations", destinationRepository.count());
        metricsDto.setEntities(entityStats);

        return metricsDto;
    }

}
