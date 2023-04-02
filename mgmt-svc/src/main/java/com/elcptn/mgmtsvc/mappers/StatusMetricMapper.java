package com.elcptn.mgmtsvc.mappers;

import com.elcptn.common.pojos.StatusMetric;
import com.elcptn.mgmtsvc.dto.StatusMetricDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/* @author: kc, created on 4/2/23 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface StatusMetricMapper {

    StatusMetricDto toDto(StatusMetric metric);

    List<StatusMetricDto> toDtoList(List<StatusMetric> metricList);
}
