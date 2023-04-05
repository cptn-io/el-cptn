package com.elcptn.mgmtsvc.mappers;

import com.elcptn.common.entities.Pipeline;
import com.elcptn.common.entities.PipelineSchedule;
import com.elcptn.mgmtsvc.dto.PipelineDto;
import com.elcptn.mgmtsvc.dto.PipelineScheduleDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PipelineScheduleMapper {
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "lastRunAt")
    @Mapping(ignore = true, target = "nextRunAt")
    @Mapping(target = "pipeline",
            expression = "java(pipelineDtoToPipeline(pipelineScheduleDto.getPipeline()))")
    PipelineSchedule toEntity(PipelineScheduleDto pipelineScheduleDto);

    PipelineScheduleDto toDto(PipelineSchedule pipelineSchedule);

    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "lastRunAt")
    @Mapping(ignore = true, target = "nextRunAt")
    @Mapping(ignore = true, target = "pipeline")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PipelineSchedule partialUpdate(PipelineScheduleDto pipelineScheduleDto, @MappingTarget PipelineSchedule pipelineSchedule);

    @SuppressWarnings("unused")
    default Pipeline pipelineDtoToPipeline(PipelineDto pipelineDto) {
        Pipeline pipeline = new Pipeline();
        pipeline.setId(pipelineDto.getId());
        return pipeline;
    }
}