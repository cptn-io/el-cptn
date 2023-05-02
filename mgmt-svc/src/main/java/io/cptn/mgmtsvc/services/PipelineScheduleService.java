package io.cptn.mgmtsvc.services;

import io.cptn.common.entities.Pipeline;
import io.cptn.common.entities.PipelineSchedule;
import io.cptn.common.exceptions.BadRequestException;
import io.cptn.common.repositories.PipelineScheduleRepository;
import io.cptn.common.services.CommonService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/* @author: kc, created on 4/4/23 */
@Service
@RequiredArgsConstructor
public class PipelineScheduleService extends CommonService {

    private final PipelineScheduleRepository pipelineScheduleRepository;

    private final PipelineService pipelineService;

    public PipelineSchedule createSchedule(PipelineSchedule pipelineSchedule) {
        validate(pipelineSchedule);

        //check if a schedule exists for the pipeline
        Long scheduleCount = pipelineScheduleRepository.countByPipelineId(pipelineSchedule.getPipeline().getId());
        if (scheduleCount > 0) {
            throw new BadRequestException("Schedule already exists for the pipeline");
        }

        pipelineSchedule.computeNextRunAt();
        return pipelineScheduleRepository.save(pipelineSchedule);
    }


    public PipelineSchedule updateSchedule(PipelineSchedule pipelineSchedule) {
        validate(pipelineSchedule);
        pipelineSchedule.computeNextRunAt();
        return pipelineScheduleRepository.save(pipelineSchedule);
    }


    public Optional<PipelineSchedule> getById(UUID id) {
        return pipelineScheduleRepository.findById(id);
    }

    public List<PipelineSchedule> getSchedulesByPipeline(UUID id) {
        return pipelineScheduleRepository.findByPipelineId(id);
    }

    public void deleteSchedule(UUID id) {
        pipelineScheduleRepository.deleteById(id);
    }

    private void validate(@NonNull PipelineSchedule pipelineSchedule) {
        List<FieldError> errors = new ArrayList<>();
        Pipeline pipeline = pipelineSchedule.getPipeline();

        if (pipeline == null) {
            errors.add(new FieldError("pipelineSchedule", "pipeline", "Pipeline is required"));
        } else {
            UUID pipelineId = pipeline.getId();

            Optional<Pipeline> pipelineOptional = pipelineService.getById(pipelineId);
            if (pipelineOptional.isEmpty()) {
                errors.add(new FieldError("pipelineSchedule", "pipeline", "Pipeline not found with provided ID"));
            } else {
                pipeline = pipelineOptional.get();
                if (!pipeline.getBatchProcess()) {
                    errors.add(new FieldError("pipelineSchedule", "pipeline", "Pipeline does not support batch processing"));
                }

            }
        }


        if (errors.size() > 0) {
            throw new BadRequestException("Invalid data", errors);
        }
    }

}
