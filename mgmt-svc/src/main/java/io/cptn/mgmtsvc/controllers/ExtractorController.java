package io.cptn.mgmtsvc.controllers;

import io.cptn.common.entities.Extractor;
import io.cptn.common.exceptions.BadRequestException;
import io.cptn.common.exceptions.NotFoundException;
import io.cptn.common.validation.OnCreate;
import io.cptn.common.validation.OnUpdate;
import io.cptn.mgmtsvc.dto.ExtractorDto;
import io.cptn.mgmtsvc.mappers.ExtractorMapper;
import io.cptn.mgmtsvc.services.ExtractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/* @author: kc, created on 6/14/23 */
@RestController
@RequiredArgsConstructor
public class ExtractorController {

    private final ExtractorService extractorService;

    private final ExtractorMapper mapper;

    @PostMapping("/api/extractor")
    public ResponseEntity<ExtractorDto> create(@Validated(OnCreate.class) @RequestBody ExtractorDto extractorDto,
                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        Extractor extractor = extractorService.create(convert(extractorDto));
        return ResponseEntity.ok(convert(extractor));
    }

    @PutMapping("/api/extractor/{id}")
    public ResponseEntity<ExtractorDto> updateAction(@PathVariable UUID id, @Validated(OnUpdate.class) @RequestBody ExtractorDto extractorDto,
                                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        Extractor extractor = getById(id);

        mapper.partialUpdate(extractorDto, extractor);

        return ResponseEntity.ok(convert(extractorService.update(extractor, extractorDto.getConfig() != null)));
    }

    @GetMapping("/api/extractor/{id}")
    public ResponseEntity<ExtractorDto> getExtractorById(@PathVariable UUID id) {
        Extractor extractor = getById(id);
        return ResponseEntity.ok(convert(extractor));
    }

    @GetMapping("/api/extractor/source/{sourceId}")
    public ResponseEntity<ExtractorDto> getExtractorBySourceId(@PathVariable UUID sourceId) {
        Extractor extractor = extractorService.getBySource(sourceId).orElseThrow(() -> new NotFoundException("Extractor not found"));
        return ResponseEntity.ok(convert(extractor));
    }

    @DeleteMapping("/api/extractor/{id}")
    public ResponseEntity<Void> deleteExtractorById(@PathVariable UUID id) {
        Extractor extractor = getById(id);
        extractorService.delete(extractor);
        return ResponseEntity.noContent().build();
    }

    private Extractor getById(UUID id) {
        return extractorService.getById(id).orElseThrow(() -> new NotFoundException("Extractor not found"));
    }

    private Extractor convert(ExtractorDto extractorDto) {
        return mapper.toEntity(extractorDto);
    }

    private ExtractorDto convert(Extractor extractor) {
        return mapper.toDto(extractor);
    }

}
