package io.cptn.ingestionsvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cptn.common.entities.InboundWriteEvent;
import io.cptn.common.entities.Source;
import io.cptn.common.exceptions.NotFoundException;
import io.cptn.common.exceptions.UnauthorizedException;
import io.cptn.common.helpers.JsonHelper;
import io.cptn.common.pojos.Header;
import io.cptn.ingestionsvc.controllers.InboundWriteEventController;
import io.cptn.ingestionsvc.dto.InboundWriteEventDto;
import io.cptn.ingestionsvc.mappers.InboundWriteEventMapper;
import io.cptn.ingestionsvc.services.InboundWriteEventService;
import io.cptn.ingestionsvc.services.SourceService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/* @author: kc, created on 5/23/23 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InboundEventControllerTests {

    private InboundWriteEventController controller;
    @Mock
    private SourceService sourceService;
    @Mock
    private InboundWriteEventService inboundWriteEventService;

    @Mock
    private HttpServletRequest request;

    @Autowired
    private InboundWriteEventMapper mapper;

    @BeforeEach
    void setup() {
        this.controller = new InboundWriteEventController(sourceService, inboundWriteEventService, mapper);
    }

    @Test
    void invalidSourceTest() {

        UUID sourceId = UUID.randomUUID();
        when(sourceService.getById(sourceId)).thenReturn(Optional.empty());
        NotFoundException e = assertThrows(NotFoundException.class, () -> controller.createEvent(sourceId, null, null));
        assertEquals("Source not found with passed ID", e.getMessage());
    }

    @Test
    void inactiveSourceTest() {
        Source source = getSource();
        source.setActive(false);
        UUID sourceId = source.getId();
        when(sourceService.getById(sourceId)).thenReturn(Optional.of(source));
        NotFoundException e = assertThrows(NotFoundException.class, () -> controller.createEvent(sourceId, null, null));
        assertEquals("Source not active", e.getMessage());
    }

    @Test
    void verifySecurityWithInvalidAuthHeaderTest() {
        Source source = getSource();
        UUID sourceId = source.getId();
        source.setupNewKeys();
        when(sourceService.getById(sourceId)).thenReturn(Optional.of(source));
        when(request.getHeader("Authorization")).thenReturn("invalid");
        when(request.getParameter("token")).thenReturn(null);

        UnauthorizedException e = assertThrows(UnauthorizedException.class, () -> controller.createEvent(sourceId,
                null, request));
        assertEquals("Unauthorized", e.getMessage());
    }

    @Test
    void verifySecurityWithInvalidTokenParamTest() {
        Source source = getSource();
        UUID sourceId = source.getId();
        source.setupNewKeys();
        when(sourceService.getById(sourceId)).thenReturn(Optional.of(source));
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getParameter("token")).thenReturn("invalid");

        UnauthorizedException e = assertThrows(UnauthorizedException.class, () -> controller.createEvent(sourceId,
                null, request));
        assertEquals("Unauthorized", e.getMessage());
    }

    @Test
    void verifyDisabledSecurityTest() {
        Source source = getSource();
        source.setSecured(false);
        UUID sourceId = source.getId();
        JsonNode payload = getPayload();

        when(sourceService.getById(sourceId)).thenReturn(Optional.of(source));

        InboundWriteEvent event = new InboundWriteEvent();
        event.setPayload(payload);
        event.setSource(source);

        UUID eventId = UUID.randomUUID();
        when(inboundWriteEventService.create(event)).thenAnswer(
                invocation -> {
                    InboundWriteEvent e = invocation.getArgument(0);
                    e.setId(eventId);
                    return e;
                }
        );

        ResponseEntity<InboundWriteEventDto> response = controller.createEvent(sourceId, payload, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(sourceId, response.getBody().getSourceId());
        assertEquals(eventId, response.getBody().getId());
        assertEquals(List.of("bar-value"), response.getHeaders().get("x-foo"));
    }

    @Test
    void verifyValidSecurityAuthPKAndSKTest() {
        Source source = getSource();
        source.setSecured(true);
        source.setupNewKeys();

        UUID sourceId = source.getId();
        JsonNode payload = getPayload();

        when(sourceService.getById(sourceId)).thenReturn(Optional.of(source));
        when(request.getHeader("Authorization")).thenReturn(source.getPrimaryKey());

        InboundWriteEvent event = new InboundWriteEvent();
        event.setPayload(payload);
        event.setSource(source);

        UUID eventId = UUID.randomUUID();
        when(inboundWriteEventService.create(event)).thenAnswer(
                invocation -> {
                    InboundWriteEvent e = invocation.getArgument(0);
                    e.setId(eventId);
                    return e;
                }
        );

        //try with primary key
        ResponseEntity<InboundWriteEventDto> response = controller.createEvent(sourceId, payload, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(sourceId, response.getBody().getSourceId());
        assertEquals(eventId, response.getBody().getId());
        assertEquals(List.of("bar-value"), response.getHeaders().get("x-foo"));

        //try with secondary key
        when(request.getHeader("Authorization")).thenReturn(source.getSecondaryKey());
        response = controller.createEvent(sourceId, payload, request);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void verifyValidSecurityTokenPKAndSKTest() {
        Source source = getSource();
        source.setSecured(true);
        source.setupNewKeys();

        UUID sourceId = source.getId();
        JsonNode payload = getPayload();

        when(sourceService.getById(sourceId)).thenReturn(Optional.of(source));
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getParameter("token")).thenReturn(source.getPrimaryKey());

        InboundWriteEvent event = new InboundWriteEvent();
        event.setPayload(payload);
        event.setSource(source);

        UUID eventId = UUID.randomUUID();
        when(inboundWriteEventService.create(event)).thenAnswer(
                invocation -> {
                    InboundWriteEvent e = invocation.getArgument(0);
                    e.setId(eventId);
                    return e;
                }
        );

        //try with primary key
        ResponseEntity<InboundWriteEventDto> response = controller.createEvent(sourceId, payload, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(sourceId, response.getBody().getSourceId());
        assertEquals(eventId, response.getBody().getId());
        assertEquals(List.of("bar-value"), response.getHeaders().get("x-foo"));

        //try with secondary key
        when(request.getParameter("token")).thenReturn(source.getSecondaryKey());
        response = controller.createEvent(sourceId, payload, request);

        assertEquals(200, response.getStatusCodeValue());
    }


    private Source getSource() {
        Header header = new Header();
        header.setKey("x-foo");
        header.setValue("bar-value");

        Source source = new Source();
        source.setId(UUID.randomUUID());
        source.setActive(true);
        source.setHeaders(List.of(header));
        source.setSecured(true);
        return source;
    }

    private JsonNode getPayload() {
        ObjectMapper objectMapper = JsonHelper.getMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("foo", "bar");

        return objectNode;
    }
}
