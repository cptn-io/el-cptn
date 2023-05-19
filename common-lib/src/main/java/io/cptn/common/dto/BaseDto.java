package io.cptn.common.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.cptn.common.exceptions.BadRequestException;
import io.cptn.common.helpers.JsonHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

/* @author: kc, created on 2/7/23 */

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class BaseDto implements Serializable {

    @EqualsAndHashCode.Include
    private UUID id;

    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;

    private String createdBy;

    private String updatedBy;

    protected JsonNode deserializeJson(String serializablePayload) {
        if (serializablePayload == null) {
            return null;
        }
        try {
            return JsonHelper.getMapper().readTree(serializablePayload);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Failed to deserialize payload");
        }
    }

    public String serializeJson(JsonNode payload) {
        if (payload == null) {
            return null;
        }

        try {
            return JsonHelper.getMapper().writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Failed to serialize payload");
        }
    }
}
