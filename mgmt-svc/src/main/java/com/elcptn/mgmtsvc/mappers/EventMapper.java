package com.elcptn.mgmtsvc.mappers;

import com.elcptn.mgmtsvc.dto.EventDto;
import com.elcptn.mgmtsvc.entities.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/* @author: kc, created on 2/8/23 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface EventMapper {
    @Mapping(source = "source.id", target = "sourceId")
    EventDto eventToEventDto(Event event);


}
