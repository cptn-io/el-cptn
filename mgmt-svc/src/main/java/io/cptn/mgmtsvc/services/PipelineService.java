package io.cptn.mgmtsvc.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.querydsl.core.types.Predicate;
import io.cptn.common.entities.*;
import io.cptn.common.exceptions.BadRequestException;
import io.cptn.common.exceptions.NotFoundException;
import io.cptn.common.repositories.PipelineRepository;
import io.cptn.common.repositories.PipelineTriggerRepository;
import io.cptn.common.services.CommonService;
import io.cptn.common.web.ListEntitiesParam;
import io.cptn.mgmtsvc.dto.TransformationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

import java.util.*;

/* @author: kc, created on 3/7/23 */
@Service
@RequiredArgsConstructor
public class PipelineService extends CommonService {
    private static final ObjectMapper mapper = new ObjectMapper();

    private final PipelineRepository pipelineRepository;

    private final SourceService sourceService;

    private final DestinationService destinationService;

    private final TransformationService transformationService;

    private final PipelineTriggerRepository pipelineTriggerRepository;

    public Pipeline create(Pipeline pipeline) {
        validateOnCreate(pipeline);

        ObjectNode defaultEdge = mapper.createObjectNode();
        defaultEdge.put("source", pipeline.getSource().getId().toString());
        defaultEdge.put("target", pipeline.getDestination().getId().toString());
        ArrayNode edges = mapper.createArrayNode();
        edges.add(defaultEdge);

        ObjectNode positions = mapper.createObjectNode();

        ObjectNode transformationMap = mapper.createObjectNode();
        transformationMap.putIfAbsent("positions", positions);
        transformationMap.putIfAbsent("edgeMap", edges);
        transformationMap.putIfAbsent("route", mapper.createArrayNode());
        pipeline.setTransformationMap(transformationMap);
        return save(pipeline);
    }

    public Optional<Pipeline> getById(UUID id) {
        return pipelineRepository.findById(id);
    }

    public List<Pipeline> getAll(ListEntitiesParam param) {
        Pageable pageable = getPageable(param);
        return pipelineRepository.findAll(pageable).stream().toList();
    }

    public List<Pipeline> getAll(ListEntitiesParam param, Predicate predicate) {
        Pageable pageable = getPageable(param);

        return pipelineRepository.findAll(predicate, pageable).stream().toList();
    }

    @CacheEvict(value = "pipeline-proc", key = "#pipeline.id")
    public Pipeline update(Pipeline pipeline) {
        validateAndComputeRoute(pipeline);
        return save(pipeline);
    }

    @CacheEvict(value = "pipeline-proc", key = "#pipeline.id")
    public void delete(Pipeline pipeline) {
        pipelineRepository.delete(pipeline);
    }

    private Pipeline save(Pipeline pipeline) {
        return pipelineRepository.save(pipeline);
    }


    public long count() {
        return count(null);
    }

    public long count(Predicate predicate) {
        if (predicate == null) {
            return pipelineRepository.count();
        }

        return pipelineRepository.count(predicate);
    }

    public void runPipeline(Pipeline pipeline) {
        Long queuedTriggers =
                pipelineTriggerRepository.countByPipelineIdAndStateEquals(pipeline.getId(),
                        State.QUEUED);

        if (queuedTriggers > 0) {
            throw new BadRequestException("Pipeline already has an existing run queued. Please wait for it to complete.");
        }

        PipelineTrigger pipelineTrigger = new PipelineTrigger();
        pipelineTrigger.setPipeline(pipeline);
        pipelineTriggerRepository.save(pipelineTrigger);
    }

    private void validateAndComputeRoute(Pipeline pipeline) {
        Map<String, String> edges = extractEdgesFromPipeline(pipeline);

        String source = pipeline.getSource().getId().toString();
        String destination = pipeline.getDestination().getId().toString();

        ArrayNode route = computeRoute(pipeline, source, destination, edges);

        pipeline.setRoute(route);
    }

    private Map<String, String> extractEdgesFromPipeline(Pipeline pipeline) {
        JsonNode transformationMap = pipeline.getTransformationMap();
        ArrayNode edgeMap = (ArrayNode) transformationMap.get("edgeMap");

        return getEdges(edgeMap);
    }

    private ArrayNode computeRoute(Pipeline pipeline, String source, String destination, Map<String, String> edges) {
        ArrayNode route = mapper.createArrayNode();
        String currentNode = source;

        Set<String> visited = new HashSet<>();
        Queue<String> queue = new ArrayDeque<>();
        queue.add(currentNode);

        while (!queue.isEmpty()) {
            currentNode = queue.poll();

            if (currentNode.equals(destination)) {
                return route;
            }

            if (!currentNode.equals(source)) {
                Transformation currentTransformation = new Transformation();
                currentTransformation.setId(UUID.fromString(currentNode));

                if (!pipeline.getTransformations().contains(currentTransformation)) {
                    throw new BadRequestException("Transformation reference is missing for " + currentNode);
                }
                route.add(currentNode);
            }

            if (visited.contains(currentNode)) {
                throw new BadRequestException("A loop exists in the pipeline. Loops are not supported.");
            }
            visited.add(currentNode);

            String nextNode = edges.get(currentNode);

            if (nextNode == null) {
                throw new BadRequestException("The pipeline does not have a valid route from Source to Destination.");
            }
            queue.add(nextNode);
        }

        throw new BadRequestException("A valid route does not exist from Source to Destination.");
    }

    private Map<String, String> getEdges(ArrayNode edgeMap) {
        Map<String, String> edges = Maps.newHashMap();
        edgeMap.forEach(edge -> {
            String source = edge.get("source").textValue();
            String target = edge.get("target").textValue();
            if (edges.containsKey(source)) {
                throw new BadRequestException("Invalid transformation Map. Multiple edges exist for source node " + source);
            }
            edges.put(source, target);
        });
        return edges;
    }

    private void validateOnCreate(Pipeline pipeline) {
        List<FieldError> fieldErrorList = new ArrayList<>();

        validateSource(pipeline, fieldErrorList);
        validateDestination(pipeline, fieldErrorList);

        if (!fieldErrorList.isEmpty()) {
            throw new BadRequestException("Invalid data", fieldErrorList);
        }
    }


    private void validateSource(Pipeline pipeline, List<FieldError> fieldErrorList) {
        Source source = pipeline.getSource();
        if (source == null || source.getId() == null) {
            fieldErrorList.add(new FieldError(CoreEntities.PIPELINE, CoreEntities.SOURCE,
                    "Source is required"));
        } else {
            Optional<Source> sourceOptional = sourceService.getById(source.getId());

            if (sourceOptional.isEmpty()) {
                fieldErrorList.add(new FieldError(CoreEntities.PIPELINE, CoreEntities.SOURCE,
                        "Source not found with provided ID"));
            } else {
                pipeline.setSource(sourceOptional.get());
            }
        }
    }

    private void validateDestination(Pipeline pipeline, List<FieldError> fieldErrorList) {
        Destination destination = pipeline.getDestination();

        if (destination == null || destination.getId() == null) {
            fieldErrorList.add(new FieldError(CoreEntities.PIPELINE, CoreEntities.DESTINATION,
                    "Destination is required"));
        } else {
            Optional<Destination> destinationOptional = destinationService.getById(destination.getId());

            if (destinationOptional.isEmpty()) {
                fieldErrorList.add(new FieldError(CoreEntities.PIPELINE, CoreEntities.DESTINATION,
                        "Destination not found with provided ID"));
            } else {
                pipeline.setDestination(destinationOptional.get());
            }
        }
    }

    public void addTransformations(Pipeline pipeline, List<TransformationDto> transformationDtoList) {
        pipeline.getTransformations().clear();
        for (TransformationDto transformationDto : transformationDtoList) {
            Transformation transformation = getTransformation(transformationDto.getId());
            pipeline.addTransformation(transformation);
        }
    }

    private Transformation getTransformation(UUID id) {
        Optional<Transformation> transformationOptional = transformationService.getById(id);
        if (transformationOptional.isEmpty()) {
            throw new NotFoundException("Transformation not found with the passed id");
        }
        return transformationOptional.get();
    }

}
