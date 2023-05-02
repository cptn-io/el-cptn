package io.cptn.ingestionsvc.mappers;

import io.cptn.common.entities.InboundWriteEvent;
import io.cptn.ingestionsvc.dto.InboundWriteEventDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/* @author: kc, created on 2/8/23 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface InboundWriteEventMapper {

    @Mapping(source = "source.id", target = "sourceId")
    InboundWriteEventDto toDto(InboundWriteEvent event);
}
