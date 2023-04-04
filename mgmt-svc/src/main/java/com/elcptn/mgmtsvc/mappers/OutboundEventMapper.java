package com.elcptn.mgmtsvc.mappers;

import com.elcptn.common.entities.OutboundEvent;
import com.elcptn.mgmtsvc.dto.OutboundEventDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/* @author: kc, created on 4/3/23 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface OutboundEventMapper {

    OutboundEventDto toDto(OutboundEvent event);

}
