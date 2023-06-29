package io.cptn.mgmtsvc.services;

import io.cptn.common.entities.Extractor;
import io.cptn.common.entities.ExtractorSchedule;
import io.cptn.common.exceptions.BadRequestException;
import io.cptn.common.repositories.ExtractorScheduleRepository;
import io.cptn.common.services.CommonService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/* @author: kc, created on 6/28/23 */
@Service
@RequiredArgsConstructor
public class ExtractorScheduleService extends CommonService {

    private final ExtractorScheduleRepository extractorScheduleRepository;

    private final ExtractorService extractorService;

    public ExtractorSchedule createSchedule(ExtractorSchedule extractorSchedule) {
        validate(extractorSchedule);

        //check if a schedule exists for the extractor
        Long scheduleCount = extractorScheduleRepository.countByExtractorId(extractorSchedule.getExtractor().getId());
        if (scheduleCount > 0) {
            throw new BadRequestException("Schedule already exists for the extractor");
        }

        extractorSchedule.computeNextRunAt();
        return extractorScheduleRepository.save(extractorSchedule);
    }


    public ExtractorSchedule updateSchedule(ExtractorSchedule extractorSchedule) {
        validate(extractorSchedule);
        extractorSchedule.computeNextRunAt();
        return extractorScheduleRepository.save(extractorSchedule);
    }


    public Optional<ExtractorSchedule> getById(UUID id) {
        return extractorScheduleRepository.findById(id);
    }

    public List<ExtractorSchedule> getSchedulesByExtractor(UUID id) {
        return extractorScheduleRepository.findByExtractorId(id);
    }

    public void deleteSchedule(UUID id) {
        extractorScheduleRepository.deleteById(id);
    }

    private void validate(@NonNull ExtractorSchedule extractorSchedule) {
        List<FieldError> errors = new ArrayList<>();
        Extractor extractor = extractorSchedule.getExtractor();

        if (extractor == null) {
            errors.add(new FieldError(CoreEntities.EXTRACTOR_SCHEDULE, CoreEntities.EXTRACTOR,
                    "Extractor is required"));
        } else {
            UUID extractorId = extractor.getId();
            Optional<Extractor> extractorOptional = extractorService.getById(extractorId);
            if (extractorOptional.isEmpty()) {
                errors.add(new FieldError(CoreEntities.EXTRACTOR_SCHEDULE, CoreEntities.EXTRACTOR, "Extractor not " +
                        "found with provided ID"));
            }
        }

        if (!errors.isEmpty()) {
            throw new BadRequestException("Invalid data", errors);
        }
    }

}
