package io.cptn.mgmtsvc.mappers;

import io.cptn.common.entities.OutboundEvent;
import io.cptn.mgmtsvc.dto.OutboundEventDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/* @author: kc, created on 4/3/23 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface OutboundEventMapper {

    OutboundEventDto toDto(OutboundEvent event);

}
