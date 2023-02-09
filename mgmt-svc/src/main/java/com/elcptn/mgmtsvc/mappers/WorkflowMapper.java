package com.elcptn.mgmtsvc.mappers;

import com.elcptn.mgmtsvc.dto.WorkflowDto;
import com.elcptn.mgmtsvc.entities.Workflow;
import org.mapstruct.*;

/* @author: kc, created on 2/7/23 */

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface WorkflowMapper {

    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @Mapping(source = "secured", target = "secured", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(source = "active", target = "active", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    Workflow workflowDtoToWorkflow(WorkflowDto workflowDto);

    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateWorkflowFromWorkflowDto(WorkflowDto workflowDto, @MappingTarget Workflow workflow);

    WorkflowDto workflowToWorkflowDto(Workflow workflow);
}
