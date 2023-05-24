package io.cptn.ingestionsvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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

import java.util.ArrayList;
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
class InboundEventControllerBatchTests {

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
        NotFoundException e = assertThrows(NotFoundException.class, () -> controller.processEventBatch(sourceId, null, null));
        assertEquals("Source not found with passed ID", e.getMessage());
    }

    @Test
    void inactiveSourceTest() {
        Source source = getSource();
        source.setActive(false);
        UUID sourceId = source.getId();
        when(sourceService.getById(sourceId)).thenReturn(Optional.of(source));
        NotFoundException e = assertThrows(NotFoundException.class, () -> controller.processEventBatch(sourceId, null, null));
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

        UnauthorizedException e = assertThrows(UnauthorizedException.class, () -> controller.processEventBatch(sourceId,
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

        UnauthorizedException e = assertThrows(UnauthorizedException.class, () -> controller.processEventBatch(sourceId,
                null, request));
        assertEquals("Unauthorized", e.getMessage());
    }

    @Test
    void verifyDisabledSecurityTest() {
        Source source = getSource();
        source.setSecured(false);
        UUID sourceId = source.getId();
        ArrayNode arrayEvents = getPayload();

        when(sourceService.getById(sourceId)).thenReturn(Optional.of(source));

        List<UUID> eventIds = setupEvents(arrayEvents, source);

        ResponseEntity<List<InboundWriteEventDto>> response = controller.processEventBatch(sourceId, arrayEvents, request);
        performAssertions(response, sourceId, eventIds);
    }

    @Test
    void verifyValidSecurityAuthPKAndSKTest() {
        Source source = getSource();
        source.setSecured(true);
        source.setupNewKeys();

        UUID sourceId = source.getId();
        ArrayNode arrayEvents = getPayload();

        when(sourceService.getById(sourceId)).thenReturn(Optional.of(source));
        when(request.getHeader("Authorization")).thenReturn(source.getPrimaryKey());


        List<UUID> eventIds = setupEvents(arrayEvents, source);

        //try with primary key
        ResponseEntity<List<InboundWriteEventDto>> response = controller.processEventBatch(sourceId, arrayEvents, request);
        performAssertions(response, sourceId, eventIds);


        //try with secondary key
        when(request.getHeader("Authorization")).thenReturn(source.getSecondaryKey());
        response = controller.processEventBatch(sourceId, arrayEvents, request);
        performAssertions(response, sourceId, eventIds);
    }

    @Test
    void verifyValidSecurityTokenPKAndSKTest() {
        Source source = getSource();
        source.setSecured(true);
        source.setupNewKeys();

        UUID sourceId = source.getId();
        ArrayNode arrayEvents = getPayload();

        when(sourceService.getById(sourceId)).thenReturn(Optional.of(source));
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getParameter("token")).thenReturn(source.getPrimaryKey());

        List<UUID> eventIds = setupEvents(arrayEvents, source);

        //try with primary key as token
        ResponseEntity<List<InboundWriteEventDto>> response = controller.processEventBatch(sourceId, arrayEvents, request);
        performAssertions(response, sourceId, eventIds);

        //try with secondary key as token
        when(request.getParameter("token")).thenReturn(source.getSecondaryKey());
        response = controller.processEventBatch(sourceId, arrayEvents, request);
        performAssertions(response, sourceId, eventIds);
    }

    private void performAssertions(ResponseEntity<List<InboundWriteEventDto>> response, UUID sourceId, List<UUID> eventIds) {
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals(sourceId, response.getBody().get(0).getSourceId());
        assertEquals(eventIds.get(0), response.getBody().get(0).getId());
        assertEquals(sourceId, response.getBody().get(1).getSourceId());
        assertEquals(eventIds.get(1), response.getBody().get(1).getId());

        assertEquals(List.of("bar-value"), response.getHeaders().get("x-foo"));
    }

    private List<UUID> setupEvents(ArrayNode arrayEvents, Source source) {
        List<InboundWriteEvent> inboundEvents = new ArrayList<>();
        List<UUID> eventIds = new ArrayList<>();

        arrayEvents.forEach(payload -> {
            InboundWriteEvent event = new InboundWriteEvent();
            event.setPayload(payload);
            event.setSource(source);
            inboundEvents.add(event);
        });

        for (int i = 0; i < inboundEvents.size(); i++) {
            InboundWriteEvent event = inboundEvents.get(i);
            UUID eventId = UUID.randomUUID();
            eventIds.add(eventId);

            when(inboundWriteEventService.create(event)).thenAnswer(
                    invocation -> {
                        InboundWriteEvent e = invocation.getArgument(0);
                        e.setId(eventId);
                        return e;
                    }
            );
        }
        return eventIds;
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

    private ArrayNode getPayload() {
        ObjectMapper objectMapper = JsonHelper.getMapper();
        ArrayNode objectArray = objectMapper.createArrayNode();

        ObjectNode objectNode1 = objectMapper.createObjectNode();
        objectNode1.put("foo", "bar");

        ObjectNode objectNode2 = objectMapper.createObjectNode();
        objectNode2.put("bar", "baz");

        objectArray.add(objectNode1);
        objectArray.add(objectNode2);

        return objectArray;
    }
}
