package com.elcptn.mgmtsvc.controllers;

import com.elcptn.common.entities.PipelineSchedule;
import com.elcptn.common.exceptions.BadRequestException;
import com.elcptn.common.exceptions.NotFoundException;
import com.elcptn.common.validation.OnCreate;
import com.elcptn.mgmtsvc.dto.PipelineScheduleDto;
import com.elcptn.mgmtsvc.mappers.PipelineScheduleMapper;
import com.elcptn.mgmtsvc.services.PipelineScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

/* @author: kc, created on 4/4/23 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class PipelineScheduleController {

    private final PipelineScheduleService pipelineScheduleService;

    private final PipelineScheduleMapper pipelineScheduleMapper;

    @PostMapping("/api/pipeline_schedule")
    public ResponseEntity<PipelineScheduleDto> create(@Validated(OnCreate.class) @RequestBody PipelineScheduleDto pipelineScheduleDto,
                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        PipelineSchedule pipelineSchedule = convert(pipelineScheduleDto);

        return ResponseEntity.ok(convert(pipelineScheduleService.createSchedule(pipelineSchedule)));
    }

    @PutMapping("/api/pipeline_schedule/{id}")
    public ResponseEntity<PipelineScheduleDto> update(@PathVariable UUID id, @Valid @RequestBody PipelineScheduleDto pipelineScheduleDto,
                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid data", bindingResult.getFieldErrors());
        }

        PipelineSchedule pipelineSchedule = getById(id);
        pipelineScheduleMapper.partialUpdate(pipelineScheduleDto, pipelineSchedule);

        return ResponseEntity.ok(convert(pipelineScheduleService.updateSchedule(pipelineSchedule)));
    }

    @GetMapping("/api/pipeline_schedule/{id}")
    public ResponseEntity<PipelineScheduleDto> getScheduleById(@PathVariable UUID id) {
        PipelineSchedule pipelineSchedule = getById(id);
        return ResponseEntity.ok(convert(pipelineSchedule));
    }

    @GetMapping("/api/pipeline_schedule/pipeline/{id}")
    public ResponseEntity<PipelineScheduleDto> getScheduleByPipeline(@PathVariable UUID id) {
        return ResponseEntity.ok(convert(pipelineScheduleService.getScheduleByPipeline(id)));
    }


    @DeleteMapping("/api/pipeline_schedule/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        pipelineScheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    private PipelineSchedule convert(PipelineScheduleDto pipelineScheduleDto) {
        return pipelineScheduleMapper.toEntity(pipelineScheduleDto);
    }

    private PipelineScheduleDto convert(PipelineSchedule pipelineSchedule) {
        return pipelineScheduleMapper.toDto(pipelineSchedule);
    }

    private PipelineSchedule getById(UUID id) {
        Optional<PipelineSchedule> pipelineScheduleOptional = pipelineScheduleService.getById(id);
        if (pipelineScheduleOptional.isEmpty()) {
            throw new NotFoundException("Pipeline Schedule not found with the passed id");
        }

        return pipelineScheduleOptional.get();
    }
}
