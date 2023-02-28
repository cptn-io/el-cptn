package com.elcptn.mgmtsvc.mappers;

import com.elcptn.mgmtsvc.dto.ActionDto;
import com.elcptn.mgmtsvc.entities.Action;
import com.elcptn.mgmtsvc.entities.Source;
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
    @Mapping(target = "sources", expression = "java(sourceIdsToSources(actionDto.getSourceIds()))")
    Action actionDtoToAction(ActionDto actionDto);


    @Mapping(target = "sourceIds", expression = "java(sourcesToSourceIds(action.getSources()))")
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
    @Mapping(target = "sources", expression = "java(mergeSourceIds(action.getSources(), actionDto" +
            ".getSourceIds()))")
    Action mergeActionSourcesfromActionDto(ActionDto actionDto, @MappingTarget Action action);

    default Set<UUID> sourcesToSourceIds(Set<Source> sources) {
        return sources.stream().map(com.elcptn.mgmtsvc.entities.Source::getId).collect(Collectors.toSet());
    }

    default Set<Source> sourceIdsToSources(Set<UUID> sourceIds) {
        return sourceIds.stream().map(com.elcptn.mgmtsvc.entities.Source::new).collect(Collectors.toSet());
    }

    default Set<Source> mergeSourceIds(Set<Source> sources, Set<UUID> SourceIds) {
        sources.addAll(sourceIdsToSources(SourceIds));
        return sources;
    }
}
