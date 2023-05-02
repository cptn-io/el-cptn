package io.cptn.mgmtsvc.mappers;

import io.cptn.common.entities.InboundEvent;
import io.cptn.common.entities.InboundWriteEvent;
import io.cptn.mgmtsvc.dto.InboundEventDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/* @author: kc, created on 4/3/23 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface InboundEventMapper {

    InboundEventDto toDto(InboundEvent event);

    InboundEventDto toDto(InboundWriteEvent event);

}
