package io.cptn.mgmtsvc.mappers;

import io.cptn.common.entities.Extractor;
import io.cptn.common.entities.ExtractorSchedule;
import io.cptn.mgmtsvc.dto.ExtractorDto;
import io.cptn.mgmtsvc.dto.ExtractorScheduleDto;
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
            expression = "java(extractorDtoToExtractor(extractorScheduleDto.getExtractor()))")
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
    ExtractorSchedule partialUpdate(ExtractorScheduleDto extractorScheduleDto, @MappingTarget ExtractorSchedule extractorSchedule);

    @SuppressWarnings("unused")
    default Extractor extractorDtoToExtractor(ExtractorDto extractorDto) {
        Extractor extractor = new Extractor();
        extractor.setId(extractorDto.getId());
        return extractor;
    }
}