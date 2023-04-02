package com.elcptn.mgmtsvc.services;

import com.elcptn.common.entities.Destination;
import com.elcptn.common.entities.Pipeline;
import com.elcptn.common.entities.Source;
import com.elcptn.common.entities.Transformation;
import com.elcptn.common.exceptions.BadRequestException;
import com.elcptn.common.exceptions.NotFoundException;
import com.elcptn.common.repositories.PipelineRepository;
import com.elcptn.mgmtsvc.common.ListEntitiesParam;
import com.elcptn.mgmtsvc.dto.TransformationDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

import java.util.*;
import java.util.stream.Collectors;

/* @author: kc, created on 3/7/23 */
@Service
@RequiredArgsConstructor
public class PipelineService extends CommonService {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final PipelineRepository pipelineRepository;

    private final SourceService sourceService;

    private final DestinationService destinationService;

    private final TransformationService transformationService;

    public Pipeline create(Pipeline pipeline) {
        validateOnCreate(pipeline);

        ObjectNode defaultEdge = mapper.createObjectNode();
        defaultEdge.put("source", pipeline.getSource().getId().toString());
        defaultEdge.put("target", pipeline.getDestination().getId().toString());
        ArrayNode edges = mapper.createArrayNode();
        edges.add(defaultEdge);

        ObjectNode positions = mapper.createObjectNode();

        ObjectNode transformationMap = mapper.createObjectNode();
        transformationMap.put("positions", positions);
        transformationMap.put("edgeMap", edges);
        transformationMap.put("route", mapper.createArrayNode());
        pipeline.setTransformationMap(transformationMap);
        return save(pipeline);
    }

    public Optional<Pipeline> getById(UUID id) {
        return pipelineRepository.findById(id);
    }

    public List<Pipeline> getAll(ListEntitiesParam param) {
        Pageable pageable = getPageable(param);
        return pipelineRepository.findAll(pageable).stream().collect(Collectors.toList());
    }

    public List<Pipeline> getAll(ListEntitiesParam param, Predicate predicate) {
        Pageable pageable = getPageable(param);

        return pipelineRepository.findAll(pageable).stream().collect(Collectors.toList());
    }

    @CacheEvict(value = "pipeline-proc", key = "#pipeline.id")
    public Pipeline update(Pipeline pipeline) {
        validateOnUpdate(pipeline);
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

        return pipelineRepository.count();
    }


    private void validateAndComputeRoute(Pipeline pipeline) {
        boolean isValid = false;
        JsonNode transformationMap = pipeline.getTransformationMap();
        ArrayNode edgeMap = (ArrayNode) transformationMap.get("edgeMap");
        Map<String, String> edges = getEdges(edgeMap);
        //start from source and ensure that route exists to destination
        String source = pipeline.getSource().getId().toString();
        String destination = pipeline.getDestination().getId().toString();

        ArrayNode route = mapper.createArrayNode();
        String currentNode = source;

        HashSet<String> visited = Sets.newHashSet();
        Queue<String> queue = Queues.newArrayDeque();
        queue.add(currentNode);

        while (!queue.isEmpty()) {
            currentNode = queue.poll();

            if (!currentNode.equals(source) && !currentNode.equals(destination)) {
                //check if the transformation is valid
                Transformation currentTransformation = new Transformation();
                currentTransformation.setId(UUID.fromString(currentNode));
                if (!pipeline.getTransformations().contains(currentTransformation)) {
                    throw new BadRequestException("Transformation reference is missing for " + currentNode);
                }
                route.add(currentNode);
            }

            if (currentNode.equals(destination)) {
                isValid = true;
                break;
            }

            //there should not be duplicate nodes or loops in the pipeline
            if (visited.contains(currentNode)) {
                throw new BadRequestException("A loop exists in the pipeline. Loops are not supported.");
            }
            visited.add(currentNode);
            String nextNode = edges.get(currentNode);
            if (nextNode == null) {
                break;
            }
            queue.add(nextNode);
        }

        pipeline.setRoute(route);

        if (!isValid) {
            throw new BadRequestException("A valid route doesn't exist from Source to Destination");
        }
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

    public void addTransformations(Pipeline pipeline, List<TransformationDto> transformationDtoList) {

        transformationDtoList.stream().forEach(transformationDto -> {
            Transformation transformation = getTransformation(transformationDto.getId());
            pipeline.addTransformation(transformation);
        });
    }

    private Transformation getTransformation(UUID id) {
        Optional<Transformation> transformationOptional = transformationService.getById(id);
        if (transformationOptional.isEmpty()) {
            throw new NotFoundException("Transformation not found with the passed id");
        }
        return transformationOptional.get();
    }

}
