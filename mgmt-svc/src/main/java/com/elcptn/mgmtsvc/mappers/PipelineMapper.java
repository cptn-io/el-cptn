package com.elcptn.mgmtsvc.mappers;

import com.elcptn.mgmtsvc.dto.DestinationDto;
import com.elcptn.mgmtsvc.dto.PipelineDto;
import com.elcptn.mgmtsvc.dto.SourceDto;
import com.elcptn.mgmtsvc.entities.Destination;
import com.elcptn.mgmtsvc.entities.Pipeline;
import com.elcptn.mgmtsvc.entities.Source;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PipelineMapper {
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @Mapping(source = "active", target = "active", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "source",
            expression = "java(sourceDtoToSource(pipelineDto.getSource()))")
    @Mapping(target = "destination",
            expression = "java(destinationDtoToDestination(pipelineDto.getDestination()))")
    Pipeline toEntity(PipelineDto pipelineDto);

    @Mapping(ignore = true, target = "transformations")
    @Mapping(ignore = true, target = "transformationMap")
    PipelineDto toDto(Pipeline pipeline);

    PipelineDto toDtoWithTransformations(Pipeline pipeline);

    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Pipeline partialUpdate(PipelineDto pipelineDto, @MappingTarget Pipeline pipeline);


    default Source sourceDtoToSource(SourceDto sourceDto) {
        Source source = new Source();
        source.setId(sourceDto.getId());
        return source;
    }

    default Destination destinationDtoToDestination(DestinationDto destinationDto) {
        Destination destination = new Destination();
        destination.setId(destinationDto.getId());
        return destination;
    }
}