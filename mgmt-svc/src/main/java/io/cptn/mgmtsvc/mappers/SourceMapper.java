package io.cptn.mgmtsvc.mappers;

import io.cptn.common.entities.Source;
import io.cptn.mgmtsvc.dto.SourceDto;
import org.mapstruct.*;

/* @author: kc, created on 2/7/23 */

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SourceMapper {

    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @Mapping(source = "secured", target = "secured", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(source = "active", target = "active", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(source = "captureRemoteIP", target = "captureRemoteIP", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    Source toEntity(SourceDto sourceDto);

    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(SourceDto sourceDto, @MappingTarget Source source);


    SourceDto toDto(Source source);
}
