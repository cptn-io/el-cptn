package io.cptn.mgmtsvc.mappers;

import io.cptn.common.entities.Destination;
import io.cptn.common.projections.DestinationView;
import io.cptn.mgmtsvc.dto.DestinationDto;
import org.mapstruct.*;

/* @author: kc, created on 2/16/23 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
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
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Destination partialUpdate(DestinationDto destinationDto, @MappingTarget Destination destination);

    DestinationDto toDto(Destination destination);

    DestinationDto toDto(DestinationView destinationView);
}
