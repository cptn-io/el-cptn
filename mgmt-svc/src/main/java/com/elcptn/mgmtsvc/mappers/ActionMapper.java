package com.elcptn.mgmtsvc.mappers;

import com.elcptn.mgmtsvc.dto.ActionDto;
import com.elcptn.mgmtsvc.entities.Action;
import com.elcptn.mgmtsvc.entities.Workflow;
import org.mapstruct.*;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/* @author: kc, created on 2/16/23 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ActionMapper {

    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @Mapping(source = "active", target = "active", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "workflows", expression = "java(workflowIdsToWorkflows(actionDto.getWorkflowIds()))")
    Action actionDtoToAction(ActionDto actionDto);


    @Mapping(target = "workflowIds", expression = "java(workflowsToWorkflowIds(action.getWorkflows()))")
    ActionDto actionToActionDto(Action action);


    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Action updateActionFromActionDto(ActionDto actionDto, @MappingTarget Action action);

    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "name")
    @Mapping(ignore = true, target = "active")
    @Mapping(ignore = true, target = "script")
    @Mapping(target = "workflows", expression = "java(mergeWorkflowIds(action.getWorkflows(), actionDto" +
            ".getWorkflowIds()))")
    Action mergeActionWorkflowsfromActionDto(ActionDto actionDto, @MappingTarget Action action);

    default Set<UUID> workflowsToWorkflowIds(Set<Workflow> workflows) {
        return workflows.stream().map(Workflow::getId).collect(Collectors.toSet());
    }

    default Set<Workflow> workflowIdsToWorkflows(Set<UUID> workflowIds) {
        return workflowIds.stream().map(Workflow::new).collect(Collectors.toSet());
    }

    default Set<Workflow> mergeWorkflowIds(Set<Workflow> workflows, Set<UUID> workflowIds) {
        workflows.addAll(workflowIdsToWorkflows(workflowIds));
        return workflows;
    }
}
