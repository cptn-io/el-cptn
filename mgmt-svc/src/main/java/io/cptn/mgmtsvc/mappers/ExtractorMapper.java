package io.cptn.mgmtsvc.mappers;

import io.cptn.common.entities.Extractor;
import io.cptn.common.entities.Source;
import io.cptn.mgmtsvc.dto.ExtractorDto;
import io.cptn.mgmtsvc.dto.SourceDto;
import org.mapstruct.*;

/* @author: kc, created on 2/16/23 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExtractorMapper {

    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @Mapping(source = "active", target = "active", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "source",
            expression = "java(sourceDtoToSource(extractorDto.getSource()))")
    Extractor toEntity(ExtractorDto extractorDto);


    @Mapping(ignore = true, target = "createdAt")
    @Mapping(ignore = true, target = "updatedAt")
    @Mapping(ignore = true, target = "createdBy")
    @Mapping(ignore = true, target = "updatedBy")
    @Mapping(ignore = true, target = "id")
    @Mapping(ignore = true, target = "source")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Extractor partialUpdate(ExtractorDto extractorDto, @MappingTarget Extractor extractor);

    ExtractorDto toDto(Extractor extractor);

    default Source sourceDtoToSource(SourceDto sourceDto) {
        Source source = new Source();
        source.setId(sourceDto.getId());
        return source;
    }
}
