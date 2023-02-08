package com.elcptn.mgmtsvc.mappers;

import com.elcptn.mgmtsvc.dto.WorkflowDto;
import com.elcptn.mgmtsvc.entities.Workflow;
import org.mapstruct.*;

/* @author: kc, created on 2/7/23 */

@Mapper
public interface WorkflowMapper {

    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    Workflow convertToEntity(WorkflowDto workflowDto);

    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(WorkflowDto workflowDto, @MappingTarget Workflow workflow);

    WorkflowDto convertToDto(Workflow workflow);
}
