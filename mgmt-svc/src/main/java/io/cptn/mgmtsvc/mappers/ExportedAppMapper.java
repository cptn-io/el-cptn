package io.cptn.mgmtsvc.mappers;

import io.cptn.common.entities.App;
import io.cptn.mgmtsvc.dto.ExportedAppDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/* @author: kc, created on 5/15/23 */
@Mapper(unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE, componentModel = org.mapstruct.MappingConstants.ComponentModel.SPRING)
public interface ExportedAppMapper {

    @Mapping(target = "name", source = "name")
    ExportedAppDto toDto(App app);
}
