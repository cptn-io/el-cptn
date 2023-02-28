package com.elcptn.mgmtsvc.mappers;

import com.elcptn.mgmtsvc.dto.SourceDto;
import com.elcptn.mgmtsvc.entities.Source;
import org.mapstruct.*;

/* @author: kc, created on 2/7/23 */

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface SourceMapper {

    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @Mapping(source = "secured", target = "secured", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(source = "active", target = "active", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    Source sourceDtoToSource(SourceDto sourceDto);

    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSourceFromSourceDto(SourceDto sourceDto, @MappingTarget Source source);


    SourceDto sourceToSourceDto(Source source);
}
