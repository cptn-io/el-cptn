package io.cptn.mgmtsvc.mappers;

import io.cptn.common.entities.ExtractorSchedule;
import io.cptn.common.entities.PipelineSchedule;
import io.cptn.mgmtsvc.dto.ExtractorScheduleDto;
import io.cptn.mgmtsvc.dto.PipelineScheduleDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExtractorScheduleMapper {
    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "lastRunAt")
    @Mapping(ignore = true, target = "nextRunAt")
    @Mapping(target = "extractor",
            expression = "java(extractorScheduleDtoToExtractor(extractorScheduleDto.getExtractor()))")
    ExtractorSchedule toEntity(ExtractorScheduleDto extractorScheduleDto);

    ExtractorScheduleDto toDto(ExtractorSchedule extractorSchedule);

    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "lastRunAt")
    @Mapping(ignore = true, target = "nextRunAt")
    @Mapping(ignore = true, target = "extractor")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ExtractorSchedule partialUpdate(PipelineScheduleDto pipelineScheduleDto, @MappingTarget PipelineSchedule pipelineSchedule);

    @SuppressWarnings("unused")
    default ExtractorSchedule extractorScheduleDtoToExtractor(ExtractorScheduleDto extractorScheduleDto) {
        ExtractorSchedule extractorSchedule = new ExtractorSchedule();
        extractorSchedule.setId(extractorScheduleDto.getId());
        return extractorSchedule;
    }
}