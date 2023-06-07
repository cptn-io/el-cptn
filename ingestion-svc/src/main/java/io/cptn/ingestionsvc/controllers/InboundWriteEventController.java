package io.cptn.ingestionsvc.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cptn.common.entities.InboundWriteEvent;
import io.cptn.common.entities.Source;
import io.cptn.common.exceptions.NotFoundException;
import io.cptn.common.exceptions.UnauthorizedException;
import io.cptn.common.helpers.JsonHelper;
import io.cptn.common.pojos.Header;
import io.cptn.ingestionsvc.dto.InboundWriteEventDto;
import io.cptn.ingestionsvc.mappers.InboundWriteEventMapper;
import io.cptn.ingestionsvc.services.InboundWriteEventService;
import io.cptn.ingestionsvc.services.SourceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/* @author: kc, created on 2/8/23 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class InboundWriteEventController {

    private final SourceService sourceService;
    private final InboundWriteEventService inboundEventService;

    private final InboundWriteEventMapper inboundEventMapper;


    @PostMapping("/event/source/{sourceId}")
    public ResponseEntity<InboundWriteEventDto> createEvent(@PathVariable UUID sourceId,
                                                            @RequestBody JsonNode jsonPayload, HttpServletRequest request) {
        Source source = getSource(sourceId);

        verifySecurity(source, request);

        if (Boolean.TRUE.equals(source.getCaptureRemoteIP())) {
            this.addRemoteIp(jsonPayload, request);
        }
        InboundWriteEvent event = new InboundWriteEvent();
        event.setPayload(jsonPayload);
        event.setSource(source);

        HttpHeaders httpHeaders = getHttpHeaders(source);

        return ResponseEntity.ok().headers(httpHeaders).body(convert(inboundEventService.create(event)));
    }

    @PostMapping("/event/source/{sourceId}/batch")
    public ResponseEntity<List<InboundWriteEventDto>> processEventBatch(@PathVariable UUID sourceId,
                                                                        @RequestBody ArrayNode jsonArray,
                                                                        HttpServletRequest request) {
        Source source = getSource(sourceId);
        verifySecurity(source, request);

        List<InboundWriteEventDto> eventDtoList = new ArrayList<>();
        jsonArray.forEach(jsonNode -> {
            if (Boolean.TRUE.equals(source.getCaptureRemoteIP())) {
                this.addRemoteIp(jsonNode, request);
            }
            InboundWriteEvent event = new InboundWriteEvent();
            event.setPayload(jsonNode);
            event.setSource(source);
            eventDtoList.add(convert(inboundEventService.create(event)));
        });
        HttpHeaders httpHeaders = getHttpHeaders(source);

        return ResponseEntity.ok().headers(httpHeaders).body(eventDtoList);
    }

    private void addRemoteIp(JsonNode jsonPayload, HttpServletRequest request) {
        ObjectNode cptnNode = JsonHelper.getMapper().createObjectNode();
        cptnNode.put("remote_ip", request.getRemoteAddr());
        ((ObjectNode) jsonPayload).put("cptn", cptnNode);
    }

    private HttpHeaders getHttpHeaders(Source source) {
        HttpHeaders httpHeaders = new HttpHeaders();
        List<Header> headerList = Optional.ofNullable(source.getHeaders()).orElse(List.of());
        headerList.forEach(header -> httpHeaders.add(header.getKey(), header.getValue()));
        return httpHeaders;
    }

    private Source getSource(UUID sourceId) {
        Optional<Source> sourceOptional = sourceService.getById(sourceId);
        if (sourceOptional.isEmpty()) {
            throw new NotFoundException("Source not found with passed ID");
        }

        Source source = sourceOptional.get();
        if (!Boolean.TRUE.equals(source.getActive())) {
            throw new NotFoundException("Source not active");
        }
        return source;
    }

    private void verifySecurity(Source source, HttpServletRequest request) {

        if (Boolean.FALSE.equals(source.getSecured())) {
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String token = request.getParameter("token");
        if (source.getPrimaryKey().equals(authHeader) || source.getPrimaryKey().equals(token)) {
            return;
        }

        if (source.getSecondaryKey().equals(authHeader) || source.getSecondaryKey().equals(token)) {
            log.warn("Secondary key used for source: {}", source.getId());
            return;
        }

        throw new UnauthorizedException("Unauthorized");
    }

    private InboundWriteEventDto convert(InboundWriteEvent event) {
        return inboundEventMapper.toDto(event);
    }
}
