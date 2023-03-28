package com.elcptn.mgmtsvc.mappers;

import com.elcptn.mgmtsvc.config.JsonHelper;
import com.elcptn.mgmtsvc.dto.ConfigItemDto;
import com.elcptn.mgmtsvc.dto.DestinationDto;
import com.elcptn.mgmtsvc.entities.Destination;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import org.mapstruct.*;

import java.util.List;

/* @author: kc, created on 2/16/23 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface DestinationMapper {

    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @Mapping(source = "active", target = "active", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    Destination toEntity(DestinationDto destinationDto);


    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @Mapping(target = "config",
            expression = "java(destinationDto.getConfig() != null ? " +
                    "configItemDtoToJsonNode(destinationDto.getConfig())" +
                    ": destination.getConfig())")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Destination partialUpdate(DestinationDto destinationDto, @MappingTarget Destination destination);


    DestinationDto toDto(Destination source);

    default JsonNode configItemDtoToJsonNode(List<ConfigItemDto> configItemDtoList) {
        return JsonHelper.getMapper().valueToTree(configItemDtoList);
    }

    default List<ConfigItemDto> JsonNodeToConfigItemDto(JsonNode jsonNode) {
        return JsonHelper.getMapper().convertValue(jsonNode, new TypeReference<List<ConfigItemDto>>() {
        });
    }
}
