package io.cptn.mgmtsvc.mappers;

import io.cptn.common.pojos.StatusMetric;
import io.cptn.mgmtsvc.dto.StatusMetricDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/* @author: kc, created on 4/2/23 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface StatusMetricMapper {

    StatusMetricDto toDto(StatusMetric metric);

    List<StatusMetricDto> toDtoList(List<StatusMetric> metricList);
}
