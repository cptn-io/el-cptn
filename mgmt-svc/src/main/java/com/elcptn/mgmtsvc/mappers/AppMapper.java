package com.elcptn.mgmtsvc.mappers;

import com.elcptn.mgmtsvc.dto.AppDto;
import com.elcptn.mgmtsvc.entities.App;
import org.mapstruct.*;

/* @author: kc, created on 2/9/23 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface AppMapper {

    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    App appDtoToApp(AppDto appDto);
    
    AppDto appToAppDto(App app);

    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    App updateAppFromAppDto(AppDto appDto, @MappingTarget App app);
}
