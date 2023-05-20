package io.cptn.mgmtsvc.mappers;

import io.cptn.common.entities.SSOProfile;
import io.cptn.mgmtsvc.dto.SSOProfileDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SSOProfileMapper {

    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @Mapping(source = "active", target = "active", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    SSOProfile toEntity(SSOProfileDto ssoProfileDto);


    SSOProfileDto toDto(SSOProfile ssoProfile);

    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    SSOProfile partialUpdate(SSOProfileDto ssoProfileDto, @MappingTarget SSOProfile ssoProfile);
}