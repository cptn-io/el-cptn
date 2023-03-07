package com.elcptn.mgmtsvc.services;

import com.elcptn.mgmtsvc.entities.Destination;
import com.elcptn.mgmtsvc.entities.Pipeline;
import com.elcptn.mgmtsvc.entities.Source;
import com.elcptn.mgmtsvc.exceptions.BadRequestException;
import com.elcptn.mgmtsvc.helpers.ListEntitiesParam;
import com.elcptn.mgmtsvc.repositories.PipelineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/* @author: kc, created on 3/7/23 */
@Service
@RequiredArgsConstructor
public class PipelineService extends CommonService {

    private final PipelineRepository pipelineRepository;

    private final SourceService sourceService;

    private final DestinationService destinationService;

    public Pipeline create(Pipeline pipeline) {
        validateOnCreate(pipeline);
        return save(pipeline);
    }

    public Optional<Pipeline> getById(UUID id) {
        return pipelineRepository.findById(id);
    }

    public List<Pipeline> getAll(ListEntitiesParam param) {
        Pageable pageable = getPageable(param);
        return pipelineRepository.findAll(pageable).stream().collect(Collectors.toList());
    }

    public Pipeline update(Pipeline pipeline) {
        validateOnUpdate(pipeline);
        return save(pipeline);
    }

    public void delete(Pipeline pipeline) {
        pipelineRepository.delete(pipeline);
    }

    private Pipeline save(Pipeline pipeline) {
        return pipelineRepository.save(pipeline);
    }


    public long count() {
        return pipelineRepository.count();
    }


    private void validateOnCreate(Pipeline pipeline) {
        List<FieldError> fieldErrorList = new ArrayList<>();
        Source source = pipeline.getSource();
        Destination destination = pipeline.getDestination();
        if (source == null || source.getId() == null) {
            fieldErrorList.add(new FieldError("pipeline", "source", "Source is required"));
        } else {
            Optional<Source> sourceOptional = sourceService.getById(source.getId());

            if (sourceOptional.isEmpty()) {
                fieldErrorList.add(new FieldError("pipeline", "source", "Source not found with provided ID"));
            } else {
                pipeline.setSource(sourceOptional.get());
            }
        }

        if (destination == null || destination.getId() == null) {
            fieldErrorList.add(new FieldError("pipeline", "destination", "Destination is required"));
        } else {
            Optional<Destination> destinationOptional = destinationService.getById(destination.getId());

            if (destinationOptional.isEmpty()) {
                fieldErrorList.add(new FieldError("pipeline", "destination", "Destination not found with provided" +
                        " ID"));
            } else {
                pipeline.setDestination(destinationOptional.get());
            }
        }

        if (fieldErrorList.size() > 0) {
            throw new BadRequestException("Invalid data", fieldErrorList);
        }
    }

    private void validateOnUpdate(Pipeline pipeline) {
        List<FieldError> fieldErrorList = new ArrayList<>();
        Source source = pipeline.getSource();
        Destination destination = pipeline.getDestination();

        if (source != null) {
            if (source.getId() == null) {
                fieldErrorList.add(new FieldError("pipeline", "source", "Source is required"));
            } else {
                Optional<Source> sourceOptional = sourceService.getById(source.getId());

                if (sourceOptional.isEmpty()) {
                    fieldErrorList.add(new FieldError("pipeline", "source", "Source not found with provided ID"));
                } else {
                    pipeline.setSource(sourceOptional.get());
                }
            }
        }

        if (destination != null) {
            if (destination.getId() == null) {
                fieldErrorList.add(new FieldError("pipeline", "destination", "Destination is required"));
            } else {
                Optional<Destination> destinationOptional = destinationService.getById(destination.getId());

                if (destinationOptional.isEmpty()) {
                    fieldErrorList.add(new FieldError("pipeline", "destination", "Destination not found with provided" +
                            " ID"));
                } else {
                    pipeline.setDestination(destinationOptional.get());
                }
            }
        }

        if (fieldErrorList.size() > 0) {
            throw new BadRequestException("Invalid data", fieldErrorList);
        }
    }


}
