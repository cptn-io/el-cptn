package com.elcptn.mgmtsvc.mappers;

import com.elcptn.mgmtsvc.dto.OperationDto;
import com.elcptn.mgmtsvc.entities.Operation;
import org.mapstruct.*;

/* @author: kc, created on 2/9/23 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface OperationMapper {
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @Mapping(source = "appId", target = "app.id")
    Operation operationDtoToOperation(OperationDto operationDto);

    @Mapping(source = "app.id", target = "appId")
    OperationDto operationToOperationDto(Operation operation);

    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @Mapping(source = "appId", target = "app.id")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Operation updateOperationFromOperationDto(OperationDto operationDto, @MappingTarget Operation operation);
}
