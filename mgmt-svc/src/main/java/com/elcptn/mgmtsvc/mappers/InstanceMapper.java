package com.elcptn.mgmtsvc.mappers;

import com.elcptn.common.entities.Instance;
import com.elcptn.mgmtsvc.dto.InstanceDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/* @author: kc, created on 4/25/23 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface InstanceMapper {

    InstanceDto toDto(Instance instance);
}
