package com.elcptn.mgmtsvc.mappers;

import com.elcptn.mgmtsvc.dto.WorkflowDto;
import com.elcptn.mgmtsvc.entities.Workflow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface WorkflowMapper {
    
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    Workflow convertToEntity(WorkflowDto workflowDto);

    WorkflowDto convertToDto(Workflow workflow);
}
