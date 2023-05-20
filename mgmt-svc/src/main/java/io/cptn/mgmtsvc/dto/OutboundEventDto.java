package io.cptn.mgmtsvc.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.cptn.common.dto.BaseDto;
import io.cptn.common.entities.State;
import io.cptn.common.helpers.JsonHelper;
import lombok.*;

/* @author: kc, created on 4/3/23 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class OutboundEventDto extends BaseDto {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String payload;

    private State state = State.QUEUED;

    private String consoleLog;

    public JsonNode getPayload() {
        return JsonHelper.deserializeJson(this.payload);
    }

    public void setPayload(JsonNode payload) {
        this.payload = JsonHelper.serializeJson(payload);
    }
}
