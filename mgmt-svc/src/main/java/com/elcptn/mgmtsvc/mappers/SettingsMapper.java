package com.elcptn.mgmtsvc.mappers;

import com.elcptn.common.entities.Settings;
import com.elcptn.mgmtsvc.dto.SettingsDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/* @author: kc, created on 5/1/23 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SettingsMapper {
    SettingsDto toDto(Settings settings);
}
