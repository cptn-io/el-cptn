package com.elcptn.ingestionsvc.mappers;

import com.elcptn.common.entities.InboundWriteEvent;
import com.elcptn.ingestionsvc.dto.InboundWriteEventDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/* @author: kc, created on 2/8/23 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface InboundWriteEventMapper {

    @Mapping(source = "source.id", target = "sourceId")
    InboundWriteEventDto toDto(InboundWriteEvent event);
}
