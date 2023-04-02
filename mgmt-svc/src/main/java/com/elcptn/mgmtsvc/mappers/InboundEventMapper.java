package com.elcptn.mgmtsvc.mappers;

import com.elcptn.common.entities.InboundEvent;
import com.elcptn.common.entities.InboundWriteEvent;
import com.elcptn.mgmtsvc.dto.InboundEventDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/* @author: kc, created on 2/8/23 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface InboundEventMapper {
    @Mapping(source = "source.id", target = "sourceId")
    InboundEventDto toDto(InboundEvent event);

    @Mapping(source = "source.id", target = "sourceId")
    InboundEventDto toDto(InboundWriteEvent event);
}
