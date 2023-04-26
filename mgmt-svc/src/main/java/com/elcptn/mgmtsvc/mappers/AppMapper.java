package com.elcptn.mgmtsvc.mappers;

import com.elcptn.common.entities.App;
import com.elcptn.mgmtsvc.dto.AppDto;
import org.mapstruct.Mapper;

@Mapper(unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE, componentModel = org.mapstruct.MappingConstants.ComponentModel.SPRING)
public interface AppMapper {

    AppDto toDto(App app);
}