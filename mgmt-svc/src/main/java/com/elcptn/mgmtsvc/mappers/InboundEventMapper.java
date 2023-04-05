package com.elcptn.mgmtsvc.mappers;

import com.elcptn.common.entities.InboundEvent;
import com.elcptn.common.entities.InboundWriteEvent;
import com.elcptn.mgmtsvc.dto.InboundEventDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/* @author: kc, created on 4/3/23 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface InboundEventMapper {

    InboundEventDto toDto(InboundEvent event);

    InboundEventDto toDto(InboundWriteEvent event);

}
