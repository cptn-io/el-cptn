package io.cptn.mgmtsvc.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cptn.common.pojos.StatusMetric;
import io.cptn.common.repositories.*;
import io.cptn.mgmtsvc.dto.DashboardMetricsDto;
import io.cptn.mgmtsvc.mappers.StatusMetricMapper;
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

    private final TransformationRepository transformationRepository;

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
        entityStats.put("transformations", transformationRepository.count());
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

    @Cacheable(value = "dashboard", key = "\"pipeline\" + #destinationId + #intervalVal")
    public DashboardMetricsDto getPipelineMetrics(UUID pipelineId, Long intervalVal) {
        DashboardMetricsDto metricsDto = new DashboardMetricsDto();

        List<StatusMetric> outboundEventMetrics = outboundEventRepository.getStatusCountsForOutboundEvents(pipelineId,
                ZonedDateTime.now().minusMinutes(intervalVal));
        metricsDto.setOutbound(statusMetricMapper.toDtoList(outboundEventMetrics));

        return metricsDto;
    }
}
