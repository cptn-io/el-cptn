package io.cptn.mgmtsvc.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.cptn.common.dto.BaseDto;
import io.cptn.common.entities.State;
import io.cptn.common.helpers.JsonHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/* @author: kc, created on 4/3/23 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class InboundEventDto extends BaseDto {

    private String payload;

    private State state = State.QUEUED;

    public JsonNode getPayload() {
        return JsonHelper.deserializeJson(this.payload);
    }

    public void setPayload(JsonNode payload) {
        this.payload = JsonHelper.serializeJson(payload);
    }
}
