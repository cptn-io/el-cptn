package com.elcptn.mgmtsvc.services;

import com.elcptn.common.pojos.StatusMetric;
import com.elcptn.common.repositories.*;
import com.elcptn.mgmtsvc.dto.DashboardMetricsDto;
import com.elcptn.mgmtsvc.mappers.StatusMetricMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/* @author: kc, created on 3/9/23 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final ObjectMapper mapper = new ObjectMapper();
    
    private final PipelineRepository pipelineRepository;

    private final SourceRepository sourceRepository;

    private final DestinationRepository destinationRepository;

    private final InboundEventRepository eventRepository;

    private final OutboundEventRepository outboundEventRepository;

    private final StatusMetricMapper statusMetricMapper;

    @Cacheable(value = "dashboard", key = "\"home\" + #intervalVal")
    public DashboardMetricsDto getMetrics(Long intervalVal) {
        DashboardMetricsDto metricsDto = new DashboardMetricsDto();

        //inbound event metrics
        List<StatusMetric> inboundEventMetrics = eventRepository.getStatusCountsForEvents(ZonedDateTime.now().minusMinutes(intervalVal));
        metricsDto.setInbound(statusMetricMapper.toDtoList(inboundEventMetrics));

        List<StatusMetric> outboundEventMetrics =
                outboundEventRepository.getStatusCountsForOutboundEvents(ZonedDateTime.now().minusMinutes(intervalVal));
        metricsDto.setOutbound(statusMetricMapper.toDtoList(outboundEventMetrics));
        //entity counts
        ObjectNode entityStats = mapper.createObjectNode();
        entityStats.put("pipelines", pipelineRepository.count());
        entityStats.put("sources", sourceRepository.count());
        entityStats.put("destinations", destinationRepository.count());
        metricsDto.setEntities(entityStats);

        return metricsDto;
    }

    @Cacheable(value = "dashboard", key = "\"source\" + #sourceId + #intervalVal")
    public DashboardMetricsDto getSourceMetrics(UUID sourceId, Long intervalVal) {
        DashboardMetricsDto metricsDto = new DashboardMetricsDto();

        List<StatusMetric> inboundEventMetrics = eventRepository.getStatusCountsForEvents(sourceId,
                ZonedDateTime.now().minusMinutes(intervalVal));
        metricsDto.setInbound(statusMetricMapper.toDtoList(inboundEventMetrics));

        //entity counts
        ObjectNode entityStats = mapper.createObjectNode();
        entityStats.put("pipelines", pipelineRepository.countBySource(sourceId));
        metricsDto.setEntities(entityStats);

        return metricsDto;
    }
}
