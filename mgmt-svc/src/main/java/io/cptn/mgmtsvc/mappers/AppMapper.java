package io.cptn.mgmtsvc.mappers;

import io.cptn.common.entities.App;
import io.cptn.mgmtsvc.dto.AppDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE, componentModel = org.mapstruct.MappingConstants.ComponentModel.SPRING)
public interface AppMapper {

    @Mapping(target = "name", source = "name")
    AppDto toDto(App app);
}