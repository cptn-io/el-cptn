package io.cptn.mgmtsvc.controllers;

import io.cptn.common.entities.ExtractorSchedule;
import io.cptn.common.exceptions.BadRequestException;
import io.cptn.common.exceptions.NotFoundException;
import io.cptn.common.validation.OnCreate;
import io.cptn.mgmtsvc.dto.ExtractorScheduleDto;
import io.cptn.mgmtsvc.mappers.ExtractorScheduleMapper;
import io.cptn.mgmtsvc.services.ExtractorScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/* @author: kc, created on 4/4/23 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class ExtractorScheduleController {

    private final ExtractorScheduleService extractorScheduleService;

    private final ExtractorScheduleMapper extractorScheduleMapper;

    @PostMapping("/api/extractor_schedule")
    public ResponseEntity<ExtractorScheduleDto> create(@Validated(OnCreate.class) @RequestBody ExtractorScheduleDto extractorScheduleDto,
                                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        ExtractorSchedule extractorSchedule = convert(extractorScheduleDto);

        return ResponseEntity.ok(convert(extractorScheduleService.createSchedule(extractorSchedule)));
    }

    @PutMapping("/api/extractor_schedule/{id}")
    public ResponseEntity<ExtractorScheduleDto> update(@PathVariable UUID id,
                                                       @Valid @RequestBody ExtractorScheduleDto extractorScheduleDto,
                                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        ExtractorSchedule extractorSchedule = getById(id);
        extractorScheduleMapper.partialUpdate(extractorScheduleDto, extractorSchedule);

        return ResponseEntity.ok(convert(extractorScheduleService.updateSchedule(extractorSchedule)));
    }

    @GetMapping("/api/extractor_schedule/{id}")
    public ResponseEntity<ExtractorScheduleDto> getScheduleById(@PathVariable UUID id) {
        ExtractorSchedule extractorSchedule = getById(id);
        return ResponseEntity.ok(convert(extractorSchedule));
    }

    @GetMapping("/api/extractor_schedule/extractor/{id}")
    public ResponseEntity<List<ExtractorScheduleDto>> getSchedulesByExtractor(@PathVariable UUID id) {
        List<ExtractorScheduleDto> extractorSchedules =
                extractorScheduleService.getSchedulesByExtractor(id).stream().map(this::convert).toList();
        return ResponseEntity.ok(extractorSchedules);
    }


    @DeleteMapping("/api/extractor_schedule/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        extractorScheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    private ExtractorSchedule convert(ExtractorScheduleDto extractorScheduleDto) {
        return extractorScheduleMapper.toEntity(extractorScheduleDto);
    }

    private ExtractorScheduleDto convert(ExtractorSchedule extractorSchedule) {
        return extractorScheduleMapper.toDto(extractorSchedule);
    }

    private ExtractorSchedule getById(UUID id) {
        Optional<ExtractorSchedule> extractorScheduleOptional = extractorScheduleService.getById(id);
        if (extractorScheduleOptional.isEmpty()) {
            throw new NotFoundException("Extractor Schedule not found with the passed id");
        }

        return extractorScheduleOptional.get();
    }
}
