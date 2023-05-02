package io.cptn.mgmtsvc.mappers;

import io.cptn.common.entities.Settings;
import io.cptn.mgmtsvc.dto.SettingsDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/* @author: kc, created on 5/1/23 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SettingsMapper {
    SettingsDto toDto(Settings settings);
}
