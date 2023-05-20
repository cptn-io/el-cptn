package io.cptn.common.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cptn.common.exceptions.BadRequestException;

/* @author: kc, created on 3/27/23 */
public class JsonHelper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonHelper() {
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }


    public static JsonNode deserializeJson(String serializablePayload) {
        if (serializablePayload == null) {
            return null;
        }
        try {
            return MAPPER.readTree(serializablePayload);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Failed to deserialize payload");
        }
    }

    public static String serializeJson(JsonNode payload) {
        if (payload == null) {
            return null;
        }

        try {
            return MAPPER.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Failed to serialize payload");
        }
    }
}
