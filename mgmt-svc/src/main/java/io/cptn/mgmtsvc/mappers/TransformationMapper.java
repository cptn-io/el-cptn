package io.cptn.mgmtsvc.mappers;

import io.cptn.common.entities.Transformation;
import io.cptn.mgmtsvc.dto.TransformationDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransformationMapper {
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @Mapping(source = "active", target = "active", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    Transformation toEntity(TransformationDto transformationDto);

    TransformationDto toDto(Transformation transformation);

    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Transformation partialUpdate(TransformationDto transformationDto, @MappingTarget Transformation transformation);
}